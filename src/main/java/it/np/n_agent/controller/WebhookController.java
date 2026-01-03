package it.np.n_agent.controller;

import it.np.n_agent.github.enums.EventType;
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

    /**
     * GitHub webhook endpoint for receiving events (PUSH, PULL_REQUEST, INSTALLATION).
     * Validates HMAC signature, parses payload based on event type, and processes asynchronously.
     * Runs validation on bounded elastic scheduler to avoid blocking reactive threads.
     *
     * @param rawPayload Raw JSON webhook payload from GitHub
     * @param eventType GitHub event type header (X-GitHub-Event)
     * @param signature HMAC SHA-256 signature for payload validation (X-Hub-Signature-256)
     * @return Mono emitting ResponseEntity with success message if processing succeeds
     * @throws it.np.n_agent.exception.WebhookMainException if HMAC validation fails or event type unsupported
     */
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
                    return EventType.retrievePayload(rawPayload, eventType, requestUtility);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(payload -> { //TODO replace with a queue system for better reliability
                    webhookService.processGithubWebhook(payload, eventType)
                            .subscribe(
                            result -> log.info("Webhook processed successfully"),
                            error -> log.error("Webhook processing failed", error)
                            );
                })
                .then(Mono.just(ResponseEntity.accepted().body("WEBHOOK ACCEPTED")));
    }



}
