package it.np.n_agent.controller;

import it.np.n_agent.dto.UserSettingDto;
import it.np.n_agent.service.UserSettingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/settings")
public class UserSettingController {

    private static final Logger log = LoggerFactory.getLogger(UserSettingController.class);

    private final UserSettingService userSettingService;

    @Autowired
    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    /**
     * Retrieves user settings for a specific GitHub user ID.
     * Returns 200 OK with settings if found, 404 Not Found if user has no settings.
     * Result is cached by service layer for 50 minutes.
     *
     * @param userId GitHub user ID
     * @return Mono emitting ResponseEntity with UserSettingDto or 404 status
     */
    @GetMapping("/{user-id}")
    public Mono<ResponseEntity<UserSettingDto>> getUserSettings(@PathVariable(name = "user-id") @Valid @NotBlank Long userId) {
        log.info("Retrieving user settings for userId: {}", userId);
        return userSettingService.getUserSettings(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnSuccess(response -> {
                    if (response.getStatusCode().is2xxSuccessful())
                        log.info("=== USER SETTINGS RETRIEVED SUCCESSFULLY ===");
                    else
                        log.info("=== USER SETTINGS NOT FOUND ===");
                });
    }

    /**
     * Saves or updates user settings for a GitHub user.
     * Validates input DTO, merges with existing settings if present, invalidates cache.
     * Returns 200 OK on success.
     *
     * @param settings UserSettingDto with updated configuration
     * @return Mono emitting ResponseEntity with success message
     * @throws it.np.n_agent.exception.MongoDbException if save operation fails
     */
    @PostMapping("/save")
    public Mono<ResponseEntity<String>> saveUserSettings(@RequestBody @Valid UserSettingDto settings) {
        log.info("Received user settings for userId: {}", settings.getUserId());

        return userSettingService.updateUserSettings(settings)
                .then(Mono.just(ResponseEntity.ok("User settings saved successfully")))
                .doOnSuccess(response -> log.info("=== USER SETTINGS SAVED SUCCESSFULLY ==="));
    }
}
