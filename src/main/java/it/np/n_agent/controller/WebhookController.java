package it.np.n_agent.controller;

import it.np.n_agent.github.dto.GHWebhookPayload;
import it.np.n_agent.service.WebhookService;
import it.np.n_agent.utilities.RequestUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookService webhookService;
    private final RequestUtility requestUtility;

    @Autowired
    public WebhookController(WebhookService webhookService,  RequestUtility requestUtility) {
        this.webhookService = webhookService;
        this.requestUtility = requestUtility;
    }

    @PostMapping("/github")
    public Mono<ResponseEntity<String>> handleWebhook(
            @RequestBody String rawPayload,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestHeader("X-Hub-Signature-256") String signature
    ) {
        log.info("=== WEBHOOK RECEIVED ===");
        log.info("Event: {}", eventType);

        return Mono.fromCallable(() -> {
                    requestUtility.validateHmac(rawPayload, signature);
                    return requestUtility.parsePayload(rawPayload, GHWebhookPayload.class);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(payload ->  webhookService.processGithubWebhook(payload, eventType))
                .then(Mono.just(ResponseEntity.ok("WEBHOOK PROCESSED")))
                .doOnSuccess(response -> log.info("=== WEBHOOK PROCESSED SUCCESSFULLY ==="));

    }



}
