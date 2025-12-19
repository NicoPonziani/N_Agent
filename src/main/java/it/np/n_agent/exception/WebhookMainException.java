package it.np.n_agent.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WebhookMainException extends RuntimeException{

    private final HttpStatus status;

    public WebhookMainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public WebhookMainException(String message,HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
