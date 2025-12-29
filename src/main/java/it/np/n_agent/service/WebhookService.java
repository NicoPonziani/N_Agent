package it.np.n_agent.service;

import it.np.n_agent.ai.dto.CodeAnalysisResult;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import it.np.n_agent.github.dto.GHWebhookPrPayload;
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
    private final UserSettingService userSettingService;

    @Autowired
    public WebhookService(AiService aiService, GithubService githubService, NotificationService notificationService, UserSettingService userSettingService) {
        this.aiService = aiService;
        this.githubService = githubService;
        this.notificationService = notificationService;
        this.userSettingService = userSettingService;
    }

    public Mono<Boolean> processGithubWebhook(Object payload, String eventType) {
        return switch (EventType.fromValue(eventType)) {
            case PUSH -> handlePushEvent((GHWebhookPrPayload) payload);
            case PULL_REQUEST -> handlePullRequestEvent((GHWebhookPrPayload) payload);
            case INSTALLATION -> handleInstallationEvent((GHWebhookInstallationPaylaod) payload);
            default -> Mono.error(new WebhookMainException("Unsupported event type", HttpStatus.BAD_REQUEST));
        };
    }

    private Mono<Boolean> handleInstallationEvent(GHWebhookInstallationPaylaod payload) {
        log.info("Processing Installation event for installation ID {} action {}", payload.getInstallation().getId(),payload.getAction());
        if(ActionType.CREATED.name().equalsIgnoreCase(payload.getAction()))
            return userSettingService.buildUserSetting(payload)
                    .flatMap(userSettingService::saveUserSettings);
        else if (ActionType.DELETED.name().equalsIgnoreCase(payload.getAction()))
            return userSettingService.deleteUserSettings(payload.getInstallation().getId(),payload.getSender().getId());

        log.info("No action taken for installation event with action: {}", payload.getAction());
        return Mono.just(true);
    }

    private Mono<Boolean> handlePullRequestEvent(GHWebhookPrPayload payload) {
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

    private Mono<Boolean> handlePushEvent(GHWebhookPrPayload payload) {
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
