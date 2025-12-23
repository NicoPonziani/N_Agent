package it.np.n_agent.service;

import it.np.n_agent.github.enums.EventType;
import it.np.n_agent.github.dto.GHWebhookPayload;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.service.auth.GitHubAuthService;
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

    @Autowired
    public WebhookService(AiService aiService, GithubService githubService) {
        this.aiService = aiService;
        this.githubService = githubService;
    }

    public Mono<Boolean> processGithubWebhook(GHWebhookPayload payload, String eventType) {
        return switch (EventType.fromValue(eventType)) {
            case PUSH -> handlePushEvent(payload);
            case PULL_REQUEST -> handlePullRequestEvent(payload);
            default -> Mono.error(new WebhookMainException("Unsupported event type", HttpStatus.BAD_REQUEST));
        };
    }

    private Mono<Boolean> handlePullRequestEvent(GHWebhookPayload payload) {
        Long installationId = payload.getInstallation().getId();
        Long prNumber = payload.getPullRequest().getNumber();
        final String apiPath = payload.getPullRequest().getUrl();

        return githubService.retrieveDiff(apiPath,installationId)
                            .flatMap(aiService::analyzeDiff)
                            .flatMap(diff -> aiService.handleAiResponse(diff, prNumber, installationId))
                            .map(response -> true)
                            .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Boolean> handlePushEvent(GHWebhookPayload payload) {
        log.info("Push event not implemented yet");
        return Mono.just(false);
    }


}
