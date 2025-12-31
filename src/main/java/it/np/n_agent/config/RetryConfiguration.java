package it.np.n_agent.config;

import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Centralized configuration for retry and resilience in reactive operations.
 * Provides retry with exponential backoff for MongoDB operations and AI calls.
 */
@Configuration
public class RetryConfiguration {

    /**
     * Retry configuration for MongoDB operations.
     * - Max 3 attempts
     * - Exponential backoff: 100ms, 200ms, 400ms
     * - Retry only on transient errors (timeout, connection loss)
     */
    @Bean
    public Retry mongoRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(attempt -> 100L * (long) Math.pow(2, attempt - 1))
                .retryOnException(this::isMongoRetryableError)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        return registry.retry("mongoRetry");
    }

    /**
     * Retry configuration for AI calls (OpenAI, Anthropic, etc.).
     * - Max 2 attempts (AI calls are expensive)
     * - Backoff: 1s, 2s
     * - Retry on timeout and connection errors
     */
    @Bean
    public Retry aiRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .intervalFunction(attempt -> 1000L * (long) Math.pow(2, attempt - 1))
                .retryOnException(this::isAiRetryableError)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        return registry.retry("aiRetry");
    }

    /**
     * Retry configuration for GitHub API calls.
     * - Max 3 attempts
     * - Backoff: 500ms, 1s, 2s
     * - Handles GitHub rate limiting (retry after wait)
     */
    @Bean
    public Retry githubRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(attempt -> 500L * (long) Math.pow(2, attempt - 1))
                .retryOnException(this::isGithubRetryableError)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        return registry.retry("githubRetry");
    }

    /**
     * Generic retry spec for standard Reactor operations.
     * Useful for inline `.retryWhen(retrySpec())` usage.
     */
    @Bean
    public RetryBackoffSpec defaultRetrySpec() {
        return reactor.util.retry.Retry.backoff(3, Duration.ofMillis(100))
                .maxBackoff(Duration.ofSeconds(2))
                .filter(this::isMongoRetryableError);
    }

    /**
     * Checks if a MongoDB error is retryable (transient).
     */
    private boolean isMongoRetryableError(Throwable throwable) {
        return throwable instanceof TimeoutException
                || throwable instanceof MongoSocketException
                || throwable instanceof MongoTimeoutException
                || (throwable.getCause() != null && isMongoRetryableError(throwable.getCause()));
    }

    /**
     * Checks if an AI error is retryable.
     * Typically timeout or connection errors, not input validation errors.
     */
    private boolean isAiRetryableError(Throwable throwable) {
        return throwable instanceof TimeoutException
                || throwable instanceof java.net.SocketTimeoutException
                || throwable instanceof java.net.ConnectException
                || (throwable.getMessage() != null &&
                    (throwable.getMessage().contains("timeout") ||
                     throwable.getMessage().contains("connection")));
    }

    /**
     * Checks if a GitHub error is retryable.
     * Includes rate limiting (403) and server errors (5xx).
     */
    private boolean isGithubRetryableError(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) return false;

        // GitHub rate limiting
        if (message.contains("rate limit") || message.contains("403")) {
            return true;
        }

        // GitHub server errors
        if (message.contains("500") || message.contains("502") ||
            message.contains("503") || message.contains("504")) {
            return true;
        }

        // Timeout and connection errors
        return throwable instanceof TimeoutException
                || throwable instanceof java.net.SocketTimeoutException
                || throwable instanceof java.net.ConnectException;
    }
}
