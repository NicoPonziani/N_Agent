package it.np.n_agent.exception;

import org.springframework.http.HttpStatus;

public class MongoDbException extends WebhookMainException {
    public MongoDbException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public MongoDbException(String message, HttpStatus status) {
        super(message, status);
    }

    public MongoDbException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
}
