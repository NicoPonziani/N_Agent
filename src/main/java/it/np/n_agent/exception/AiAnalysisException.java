package it.np.n_agent.exception;

import org.springframework.http.HttpStatus;

public class AiAnalysisException extends WebhookMainException {
    public AiAnalysisException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public AiAnalysisException(String message, HttpStatus status) {
        super(message, status);
    }

    public AiAnalysisException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
}
