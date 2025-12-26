package it.np.n_agent.service;

import it.np.n_agent.ai.dto.CodeAnalysisResult;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.dto.GHWebhookPayload;
import it.np.n_agent.github.enums.ActionType;
import it.np.n_agent.github.enums.EventType;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final AiService aiService;
    private final GithubService githubService;
    private final NotificationService notificationService;

    @Autowired
    public WebhookService(AiService aiService, GithubService githubService, NotificationService notificationService) {
        this.aiService = aiService;
        this.githubService = githubService;
        this.notificationService = notificationService;
    }

    public Mono<Boolean> processGithubWebhook(GHWebhookPayload payload, String eventType) {
        return switch (EventType.fromValue(eventType)) {
            case PUSH -> handlePushEvent(payload);
            case PULL_REQUEST -> handlePullRequestEvent(payload);
            default -> Mono.error(new WebhookMainException("Unsupported event type", HttpStatus.BAD_REQUEST));
        };
    }

    private Mono<Boolean> handlePullRequestEvent(GHWebhookPayload payload) {
        log.info("Processing Pull Request event for PR #{}", payload.getPullRequest().getNumber());
        if(ActionType.CLOSED.name().equalsIgnoreCase(payload.getAction())){
            log.info("Pull Request #{} is closed. No action taken.", payload.getPullRequest().getNumber());
            return Mono.just(true);
        }

        final Long installationId = payload.getInstallation().getId();
        final Long prNumber = payload.getPullRequest().getNumber();
        final String apiPath = payload.getPullRequest().getUrl();
        final String commitSha = payload.getPullRequest().getHead().getSha();
        final String owner = payload.getPullRequest().getUser().getLogin();
        final String repo = payload.getRepository().getName();

        return githubService.retrieveDiff(apiPath,installationId)
                            .flatMap(aiService::analyzeDiff)
                            .flatMap(diff -> aiService.handleAiResponse(diff, prNumber, installationId))
                            .flatMap(response -> Mono.just(
                                    WebhookProcessResult.builder()
                                            .installationId(installationId)
                                            .commitSha(commitSha)
                                            .url(apiPath)
                                            .prNumber(prNumber)
                                            .owner(owner)
                                            .repo(repo)
                                            .analysisResult(response)
                                            .build()
                            ))
                            .flatMap(notificationService::sendNotification)
                            .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Boolean> handlePushEvent(GHWebhookPayload payload) {
        log.info("Push event not implemented yet");
        return Mono.just(false);
    }

    @Builder
    public record WebhookProcessResult(Long installationId,
                                       String commitSha,
                                       String url,
                                       Long prNumber,
                                       String owner,
                                       String repo,
                                       CodeAnalysisResult analysisResult) {

    }
}
