package it.np.n_agent.controller;

import it.np.n_agent.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/ai-models-available")
    public Mono<ResponseEntity<List<String>>> getAiModelsAvailable() {
        log.info("Retrieving available AI models");

        return configService.getAiModelsAvailable()
                .map(ResponseEntity::ok)
                .doOnSuccess(r -> log.info("=== Successfully retrieved AI models ==="));
    }
}
