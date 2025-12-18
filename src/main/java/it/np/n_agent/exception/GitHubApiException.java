package it.np.n_agent.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GitHubApiException extends WebhookMainException {

    public GitHubApiException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }

    public GitHubApiException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }

    public GitHubApiException(String message, HttpStatus statusCode, Throwable cause) {
        super(message,statusCode, cause);
    }

}
