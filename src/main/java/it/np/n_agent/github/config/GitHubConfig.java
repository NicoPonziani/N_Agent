package it.np.n_agent.github.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "github")
@Data
@Validated
public class GitHubConfig {

    @Valid
    @NotNull
    private App app;

    @Valid
    @NotNull
    private Api api;

    @Data
    @Validated
    public static class App {
        @NotNull(message = "GitHub App ID is required")
        @Positive(message = "GitHub App ID must be positive")
        private String id;

        // Private key path - optional if GITHUB_PRIVATE_KEY_BASE64 env var is set
        private String privateKeyPath;
    }

    @Data
    @Validated
    public static class Api {
        @NotBlank(message = "Base URL is required")
        private String baseUrl;

        @NotBlank(message = "Installation token URL is required")
        private String installationTokenUrl;
    }
}
