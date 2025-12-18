package it.np.n_agent.exception.handler;

import it.np.n_agent.exception.WebhookMainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WebhookMainException.class)
    public Mono<ResponseEntity<ResponseError>> handleWebhookMainException(WebhookMainException ex) {
        loggerHandle(ex);
        ResponseError error = ResponseError.builder()
                .message(ex.getMessage())
                .status(ex.getStatus().value())
                .errorType(ex.getStatus().getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .build();
        return Mono.just(ResponseEntity.status(ex.getStatus()).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ResponseError>> handleGlobalException(Exception ex) {
        loggerHandle(ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseError error = ResponseError.builder()
                .message(ex.getMessage())
                .status(status.value())
                .errorType(status.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .build();
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    private void loggerHandle(Exception ex){
        StackTraceElement origin = ex.getStackTrace()[0];
        String className = origin.getClassName();
        String methodName = origin.getMethodName();

        log.error("Handling {}: {} - Error in class {} method {}", ex.getClass().getName(), ex.getMessage(), className, methodName);
    }
}
