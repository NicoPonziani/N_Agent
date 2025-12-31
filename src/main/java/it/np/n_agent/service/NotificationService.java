package it.np.n_agent.service;

import it.np.n_agent.ai.dto.CodeAnalysisResult;
import it.np.n_agent.dto.enums.NotificationClientEnum;
import it.np.n_agent.exception.GitHubApiException;
import it.np.n_agent.github.enums.HeaderGithubUtility;
import it.np.n_agent.service.WebhookService.WebhookBaseInfo;
import it.np.n_agent.service.auth.GitHubAuthService;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.NotificationSettingsDto;
import static it.np.n_agent.utilities.CommentsUtility.formatCommentBody;
import static it.np.n_agent.utilities.CommentsUtility.formatGeneralCommentBody;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final GitHubAuthService authService;
    private final WebClient githubWebClient;

    @Autowired
    public NotificationService(GitHubAuthService authService, @Qualifier("githubWebClient") WebClient githubWebClient) {
        this.authService = authService;
        this.githubWebClient = githubWebClient;
    }

    /**
     * Sends notifications to configured channels based on user preferences.
     * Supports multiple notification clients (GitHub comments, Email, Slack).
     * Executes all enabled notification channels concurrently and aggregates results.
     *
     * @param request Webhook base info containing PR details and URLs
     * @param analysisResult AI analysis result with issues and recommendations
     * @param notificationSettingsDto User notification preferences
     * @return Mono emitting true if all notifications succeed, false otherwise
     */
    public Mono<Boolean> sendNotification(WebhookBaseInfo request, CodeAnalysisResult analysisResult, NotificationSettingsDto notificationSettingsDto){
        log.info("Sending notification for PR #{} to URL: {}", request.prNumber(), request.url());
        List<Mono<Boolean>> notifications = new ArrayList<>();

        for (NotificationClientEnum client : NotificationClientEnum.getUserClients(notificationSettingsDto)){
            switch (client){
                case GITHUB -> notifications.add(sendGithub(request, analysisResult));
                case EMAIL -> notifications.add(Mono.just(true)); //TODO implement email notification
                case SLACK -> notifications.add(Mono.just(true)); //TODO implement slack notification
                default -> log.warn("Unsupported notification client: {}", client);
            }
        }

        return Flux.concat(notifications)
                   .reduce((a, b) -> a && b);
    }

    /**
     * Sends GitHub PR review comment with analysis results.
     * Creates a review with inline comments for each detected issue.
     * Uses GitHub App installation token for authentication.
     *
     * @param request Webhook base info with PR URL and installation ID
     * @param analysisResult AI analysis result containing issues and recommendation
     * @return Mono emitting true if review posted successfully
     * @throws GitHubApiException if GitHub API call fails
     */
    private Mono<Boolean> sendGithub(WebhookBaseInfo request, CodeAnalysisResult analysisResult) {
        log.info("Preparing to send GitHub notification for PR #{}", request.prNumber());
        return authService.getInstallationToken(request.installationId())
                .flatMap(token ->
                        githubWebClient.post()
                                .uri(request.url() + "/reviews")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .header(HttpHeaders.ACCEPT, HeaderGithubUtility.APPLICATION_VND_JSON.getHeaderValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(buildNotificationRequestGithub(request, analysisResult))
                                .retrieve()
                                .bodyToMono(Void.class)
                )
                .map(ignored -> true)
                .doOnSuccess(result -> log.info("Notification sent successfully for PR #{}", request.prNumber()))
                .onErrorMap(error -> new GitHubApiException("Failed to send notification to GitHub", HttpStatus.BAD_GATEWAY, error));
    }

    /**
     * Builds GitHub PR review request payload with inline comments.
     * Maps detected issues to inline comments with file path, line number, and suggestion.
     * Creates general review comment summarizing analysis results.
     *
     * @param request Webhook base info containing commit SHA
     * @param analysisResult AI analysis result with issues list
     * @return NotificationRequest record ready for GitHub API submission
     */
    private NotificationRequest buildNotificationRequestGithub(WebhookBaseInfo request, CodeAnalysisResult analysisResult){
        log.info("Building notification request for PR #{}", request.prNumber());
        List<InlineCommentRequest> inlineCommentRequests = analysisResult.getIssues().stream()
                .map(issue -> InlineCommentRequest.builder()
                        .body(formatCommentBody(issue.getSeverity(),issue.getType(), issue.getMessage(), issue.getSuggestion()))
                        .path(issue.getFile())
                        .line(issue.getLine())
                        .side(SideLine.RIGHT.name())
                        .build()
                ).toList();

        return NotificationRequest.builder()
                .commitId(request.commitSha())
                .body(formatGeneralCommentBody(
                        analysisResult.getSummary(),
                        analysisResult.getRecommendation().name(),
                        analysisResult.getRegretProbability() * 100,
                        analysisResult.getIssues().size())
                )
                .event(analysisResult.getRecommendation().name())
                .comments(inlineCommentRequests)
                .build();
    }

    /**
     * GitHub PR review request payload.
     *
     * @param commitId Git commit SHA to associate review with
     * @param body General review comment body (markdown supported)
     * @param event Review event type (APPROVE, REQUEST_CHANGES, COMMENT)
     * @param comments List of inline comments for specific code lines
     */
    @Builder
    public record NotificationRequest(String commitId,String body, String event, List<InlineCommentRequest> comments){}

    /**
     * GitHub inline comment request for a specific line in diff.
     *
     * @param path File path relative to repository root
     * @param line Line number in the file
     * @param side Side of diff (LEFT for old, RIGHT for new)
     * @param body Comment body with issue details and suggestion
     */
    @Builder
    public record InlineCommentRequest(String path, Integer line,String side,String body){}

    /**
     * Enum representing diff side for inline comments.
     */
    public enum SideLine{
        LEFT,  // Old version (before changes)
        RIGHT  // New version (after changes)
    }
}
