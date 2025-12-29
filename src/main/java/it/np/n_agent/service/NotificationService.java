package it.np.n_agent.service;

import it.np.n_agent.exception.GitHubApiException;
import it.np.n_agent.github.enums.HeaderGithubUtility;
import it.np.n_agent.service.WebhookService.WebhookProcessResult;
import it.np.n_agent.service.auth.GitHubAuthService;
import it.np.n_agent.utilities.CommentsUtility;
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
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<Boolean> sendNotification(WebhookProcessResult request){
        log.info("Sending notification for PR #{} to URL: {}", request.prNumber(), request.url());

        return authService.getInstallationToken(request.installationId())
                .flatMap(token ->
                        githubWebClient.post()
                                .uri(request.url() + "/reviews")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .header(HttpHeaders.ACCEPT, HeaderGithubUtility.APPLICATION_VND_JSON.getHeaderValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(buildNotificationRequest(request))
                                .retrieve()
                                .bodyToMono(Void.class)
                )
                .map(ignored -> true)
                .doOnSuccess(result -> log.info("Notification sent successfully for PR #{}", request.prNumber()))
                .onErrorMap(error -> new GitHubApiException("Failed to send notification to GitHub", HttpStatus.BAD_GATEWAY, error));
    }

    private NotificationRequest buildNotificationRequest(WebhookProcessResult request){
        log.info("Building notification request for PR #{}", request.prNumber());
        List<InlineCommentRequest> inlineCommentRequests = request.analysisResult().getIssues().stream()
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
                        request.analysisResult().getSummary(),
                        request.analysisResult().getRecommendation().name(),
                        request.analysisResult().getRegretProbability() * 100,
                        request.analysisResult().getIssues().size())
                )
                .event(request.analysisResult().getRecommendation().name())
                .comments(inlineCommentRequests)
                .build();
    }

    @Builder
    public record NotificationRequest(String commitId,String body, String event, List<InlineCommentRequest> comments){}

    @Builder
    public record InlineCommentRequest(String path, Integer line,String side,String body){}

    public enum SideLine{
        LEFT,
        RIGHT
    }
}
