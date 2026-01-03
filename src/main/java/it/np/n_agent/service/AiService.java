package it.np.n_agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.np.n_agent.ai.dto.CodeAnalysisResult;
import it.np.n_agent.ai.enums.RecommendationEnum;
import it.np.n_agent.ai.functions.HistoricalIssuesFunction;
import it.np.n_agent.entity.HistoricalIssueEntity;
import it.np.n_agent.exception.AiAnalysisException;
import it.np.n_agent.exception.MongoDbException;
import it.np.n_agent.repository.IssueRepository;
import it.np.n_agent.utilities.PromptUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.AnalysisRulesDto;
import static it.np.n_agent.utilities.ResourceUtility.loadPromptAsString;

@Service
public class AiService {

    private final static Logger log = LoggerFactory.getLogger(AiService.class);

    private final ChatClient chatModel;
    private final IssueRepository issueRepository;

    public AiService(@Qualifier("OPEN_AI") ChatClient chatModel, IssueRepository issueRepository, ObjectMapper objectMapper){
        this.chatModel = chatModel;
        this.issueRepository = issueRepository;
    }

    /**
     * Analyzes code diff using AI model with configured analysis rules.
     * Sends diff to OpenAI GPT model with historical issues context via function calling.
     * Runs on bounded elastic scheduler to handle potentially blocking AI API calls.
     *
     * @param diff Git diff content to analyze
     * @param rules Analysis rules configuration (null pointer prediction, debt estimation, etc.)
     * @return Mono emitting CodeAnalysisResult with detected issues and recommendations
     * @throws AiAnalysisException if AI analysis fails
     */
    public Mono<CodeAnalysisResult> analyzeDiff(String diff, AnalysisRulesDto rules){
        log.info("Analyzing diff START");

        long startNanos = System.nanoTime();

        return Mono.fromSupplier(() ->
                        chatModel.prompt(loadPromptAsString("historical_issue_prompt.md"))
                        .user(PromptUtility.generatePullRequestPrompt("user_analysis_rules.md", rules, diff))
                        .toolCallbacks(ToolCallbacks.from(new HistoricalIssuesFunction(issueRepository)))
                        .call()
                        .entity(CodeAnalysisResult.class)
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(response -> log.info("AI analysis completed {}",response))
                .doFinally(st -> {
                    switch (st){
                        case CANCEL -> log.error("AI analysis errored after {} ms", (System.nanoTime() - startNanos) / 1_000_000);
                        case ON_ERROR -> log.error("AI analysis cancelled after {} ms", (System.nanoTime() - startNanos) / 1_000_000);
                        default -> log.info("AI analysis completed successfully after {} ms", (System.nanoTime() - startNanos) / 1_000_000);
                    }
                })
                .onErrorMap(error -> {
                    if (error instanceof java.util.concurrent.TimeoutException) {
                        return new AiAnalysisException("AI analysis timeout", HttpStatus.SERVICE_UNAVAILABLE, error);
                    }
                    return new AiAnalysisException("Failed to analyze code diff", HttpStatus.INTERNAL_SERVER_ERROR, error);
                });
    }

    /**
     * Handles AI analysis response by saving detected issues to historical database.
     * Processes recommendation (APPROVE, REQUEST_CHANGES, COMMENT) and persists issues for future predictions.
     * If recommendation is APPROVE or no issues found, returns analysis without saving.
     * Otherwise, maps issues to HistoricalIssueEntity and saves to MongoDB.
     *
     * @param analysis AI analysis result containing issues and recommendation
     * @param prNumber Pull request number for issue tracking
     * @param userInstallationId GitHub App installation ID for issue association
     * @return Mono emitting the original analysis result after saving issues
     * @throws MongoDbException if saving historical issues fails
     */
    public Mono<CodeAnalysisResult> handleAiResponse(CodeAnalysisResult analysis,Long prNumber, Long userInstallationId) {
        log.info("Handling AI response with recommendation: {} \nsummary: {}",analysis.getRecommendation(), analysis.getSummary());

        RecommendationEnum recommendation = analysis.getRecommendation();
        if (recommendation == null) {
            boolean noIssues = analysis.getIssues() == null || analysis.getIssues().isEmpty();
            recommendation = noIssues ? RecommendationEnum.APPROVE : RecommendationEnum.COMMENT;
            log.warn("AI response missing recommendation. Applying fallback={} (issuesCount={})", recommendation, analysis.getIssuesCount());
            analysis.setRecommendation(recommendation);
            if (analysis.getSummary() == null && noIssues) {
                analysis.setSummary("No issues found");
            }
        }

        return switch (recommendation) {
            case APPROVE -> {
                log.info("No issues found");
                yield Mono.just(analysis);
            }
            case REQUEST_CHANGES, COMMENT -> {
                log.info("Number of issues found: {}", analysis.getIssuesCount());

                if(analysis.getIssues() == null || analysis.getIssues().isEmpty()){
                    log.info("No issues found ✅");
                    yield Mono.just(analysis);
                }

                List<HistoricalIssueEntity> issues = analysis.getIssues().stream()
                        .map(issue -> HistoricalIssueEntity.builder()
                                .repository(issue.getFile())
                                .type(issue.getType())
                                .timeToFix(issue.getEstimatedFixTime())
                                .resolution(issue.getSuggestion())
                                .foundAt(analysis.getAnalyzedAt())
                                .prNumber(prNumber)
                                .userInstallationId(userInstallationId)
                                .build()
                        ).toList();

                yield issueRepository.saveAll(issues)
                        .doOnNext(saved -> log.info("Saved issue: {}", saved))
                        .onErrorMap(error -> new MongoDbException("Failed to save historical issues", HttpStatus.INTERNAL_SERVER_ERROR, error))
                        .then(Mono.just(analysis));
            }
        };
    }

}
