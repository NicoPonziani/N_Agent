package it.np.n_agent.service;

import it.np.n_agent.exception.GitHubApiException;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.enums.HeaderGithubUtility;
import it.np.n_agent.service.auth.GitHubAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GithubService {

    private final static Logger log = LoggerFactory.getLogger(GithubService.class);

    private final WebClient githubWebClient;
    private final GitHubAuthService authService;

    @Autowired
    public GithubService(@Qualifier("githubWebClient") WebClient githubWebClient, GitHubAuthService authService) {
        this.githubWebClient = githubWebClient;
        this.authService = authService;
    }

    public Mono<String> retrieveDiff(String apiPath, Long installationId) {
        log.info("Fetching diff from: {}", apiPath);
        return authService.getInstallationToken(installationId)
                .flatMap(installationToken ->
                        githubWebClient.get()
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
                                    .onErrorMap(error -> new GitHubApiException("Error fetching diff from GitHub", HttpStatus.BAD_GATEWAY, error))
                );
    }
}
