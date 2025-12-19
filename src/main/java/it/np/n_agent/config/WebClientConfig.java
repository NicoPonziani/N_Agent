package it.np.n_agent.config;

import it.np.n_agent.dto.github.HeaderGithubUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @Qualifier("githubWebClient")
    public WebClient githubWebClient(WebClient.Builder builder,
                                     @Value("${github.api.base-url}") String githubBaseUrl) {
        return builder
                .baseUrl(githubBaseUrl) // https://api.github.com
                .defaultHeader("Accept", HeaderGithubUtility.APPLICATION_VND_V3_JSON.getHeaderValue())
                .defaultHeader("User-Agent", "Code-Regret-Predictor/1.0")
                .build();
    }
}
