package it.np.n_agent.service;

import it.np.n_agent.dto.github.HeaderGithubUtility;
import it.np.n_agent.dto.github.EventType;
import it.np.n_agent.dto.github.GHWebhookPayload;
import it.np.n_agent.exception.WebhookMainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final WebClient githubWebClient;
    private final GitHubAuthService authService;

    @Autowired
    public WebhookService(
            @Qualifier("githubWebClient") WebClient githubWebClient,
            GitHubAuthService authService) {
        this.githubWebClient = githubWebClient;
        this.authService = authService;
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
        final String apiPath = payload.getPullRequest().getUrl();

        return authService.getInstallationToken(installationId)
                          .flatMap(installationToken -> retrieveDiff(apiPath,installationToken))
                          .map(response -> true)
                          .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Boolean> handlePushEvent(GHWebhookPayload payload) {
        log.info("Push event not implemented yet");
        return Mono.just(false);
    }


    public Mono<String> retrieveDiff(String apiPath, String installationToken){
        log.info("Fetching diff from: {}", apiPath);
        return githubWebClient.get()
                .uri(apiPath)
                .header("Accept", HeaderGithubUtility.APPLICATION_VND_V3_DIFF.getHeaderValue())
                .header("Authorization", "token " + installationToken)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> {
                            log.error("GitHub API error {}: {}", response.statusCode(), apiPath);
                            throw new WebhookMainException("Failed to fetch PR diff: " + response.statusCode(),HttpStatus.BAD_GATEWAY);
                        }
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .retry(2)
                .doOnSuccess(diffContent -> {
                    log.info("Fetched diff content: {} characters", diffContent.length());
                    log.debug("Diff preview:\n{}", diffContent.substring(0, Math.min(500, diffContent.length())));
                })
                .doOnError(error -> log.error("Error fetching diff: {}", error.getMessage()));
    }
}
