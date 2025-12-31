package it.np.n_agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * Simple health check endpoint to verify application is running.
     * Used for monitoring and deployment verification.
     *
     * @return Mono emitting health status message
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("OK - Code Regret Predictor Agent is running");
    }
}
