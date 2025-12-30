package it.np.n_agent.service;

import it.np.n_agent.ai.dto.CodeAnalysisResult;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.AnalysisRulesDto;
import static it.np.n_agent.utilities.ResourceUtility.loadPrompt;

@Service
public class AiService {

    private final static Logger log = LoggerFactory.getLogger(AiService.class);

    private final ChatClient chatModel;
    private final IssueRepository issueRepository;

    public AiService(@Qualifier("OPEN_AI") ChatClient chatModel, IssueRepository issueRepository){
        this.chatModel = chatModel;
        this.issueRepository = issueRepository;
    }

    public Mono<CodeAnalysisResult> analyzeDiff(String diff, AnalysisRulesDto rules){
        log.info("Analyzing diff START");
        return Mono.fromCallable(() ->
            chatModel.prompt(loadPrompt("historical_issue_prompt.md").getContentAsString(StandardCharsets.UTF_8))
                     .user(PromptUtility.generatePullRequestPrompt("user_analysis_rules.md", rules,diff))
                     .toolCallbacks(ToolCallbacks.from(new HistoricalIssuesFunction(issueRepository)))
                     .call()
                     .entity(CodeAnalysisResult.class)
        )
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(response -> log.info("AI analysis completed: {}",response))
        .onErrorMap(error -> new AiAnalysisException("Failed to analyze code diff", HttpStatus.INTERNAL_SERVER_ERROR, error));
    }

    public Mono<CodeAnalysisResult> handleAiResponse(CodeAnalysisResult analysis,Long prNumber, Long userInstallationId) {
        log.info("Handling AI response with recommendation: {} \nsummary: {}",analysis.getRecommendation(), analysis.getSummary());

        return switch (analysis.getRecommendation()) {
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
