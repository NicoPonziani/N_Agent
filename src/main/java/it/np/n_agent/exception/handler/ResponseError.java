package it.np.n_agent.exception.handler;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseError {

    private String message;
    private LocalDateTime timestamp;
    private int status;
    private String errorType;
}
