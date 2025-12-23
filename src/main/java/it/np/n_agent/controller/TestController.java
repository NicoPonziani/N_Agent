package it.np.n_agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("OK - Code Regret Predictor Agent is running");
    }
}
