package it.np.n_agent.service;

import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import it.np.n_agent.dto.UserSettingDto;
import it.np.n_agent.entity.*;
import it.np.n_agent.exception.MongoDbException;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import it.np.n_agent.github.dto.GHWebhookInstallationRepoPayload;
import it.np.n_agent.mapper.UserSettingMapper;
import it.np.n_agent.repository.UserSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static it.np.n_agent.utilities.UserSettingUtility.buildAccountInfo;
import static it.np.n_agent.utilities.UserSettingUtility.buildDefaultRepositories;

@Service
public class UserSettingService {

    private static final Logger log = LoggerFactory.getLogger(UserSettingService.class);

    private final UserSettingRepository userSettingRepository;
    private final UserSettingMapper userSettingMapper;
    private final Retry mongoRetry;

    @Autowired
    public UserSettingService(UserSettingRepository userSettingRepository,
                            UserSettingMapper userSettingMapper,
                            Retry mongoRetry) {
        this.userSettingRepository = userSettingRepository;
        this.userSettingMapper = userSettingMapper;
        this.mongoRetry = mongoRetry;
    }

    /**
     * Builds a UserSetting object from GitHub App installation webhook payload.
     * Called when receiving an INSTALLATION event with CREATED action.
     *
     * @param payload GitHub webhook payload containing installation data
     * @return Mono emitting UserSetting built with default values
     * @throws WebhookMainException if building fails
     */
    public Mono<UserSetting> buildUserSetting(GHWebhookInstallationPaylaod payload){
        log.info("Building user setting for installation ID: {}", payload.getInstallation().getId());

        return Mono.defer(() -> {
        Long githubInstallationId = payload.getInstallation().getId();
        Long userId = payload.getInstallation().getAccount().getId();

        UserSetting userSetting = UserSetting.builder()
                .userId(userId)
                .githubInstallationId(githubInstallationId)
                .globalSettings(GlobalSettings.builder().build())
                .repositories(buildDefaultRepositories(payload.getRepositories()))
                .account(buildAccountInfo(payload.getInstallation().getAccount()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            return Mono.just(userSetting);
        })
        .doOnSuccess(setting -> log.info("User setting built successfully for installation ID: {}", payload.getInstallation().getId()))
        .onErrorMap(error -> new WebhookMainException(
            String.format("Failed to build user setting for installation ID: %s", payload.getInstallation().getId()),
            HttpStatus.INTERNAL_SERVER_ERROR,
            error
        ));
    }

    /**
     * Removes repositories from user settings based on installation ID and list of repositories.
     * Called when receiving INSTALLATION_REPOSITORIES event with REMOVED action.
     * Applies automatic retry (max 3 attempts) and 3-second timeout.
     *
     * @param installationId GitHub App installation ID
     * @param repositories List of repositories to remove
     * @return Mono emitting true if removal succeeds
     * @throws MongoDbException if operation fails after all retries
     */
    @CacheEvict(value = "userSettings", key = "#installationId")
    public Mono<Boolean> removedRepository(Long installationId, List<GHWebhookInstallationRepoPayload.Repository> repositories){
        log.info("Removing repository {} from user settings for installation ID: {}", repositories, installationId);
        return userSettingRepository.findByGithubInstallationId(installationId)
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .switchIfEmpty(Mono.error(new WebhookMainException(
                        String.format("User settings not found for installationId: %s", installationId),
                        HttpStatus.NOT_FOUND))
                )
                .handle(sinkRemovedTriggers(repositories))
                .flatMap(this::saveUserSettings)
                .defaultIfEmpty(false)
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to remove repository from user settings for installation ID: %s", installationId),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error));
    }

    /**
     * Adds repositories to user settings based on installation ID and list of repositories.
     * Called when receiving INSTALLATION_REPOSITORIES event with ADDED action.
     * Applies automatic retry (max 3 attempts) and 3-second timeout.
     *
     * @param installationId GitHub App installation ID
     * @param repositories List of repositories to add
     * @return Mono emitting true if addition succeeds
     * @throws MongoDbException if operation fails after all retries
     */
    @CacheEvict(value = "userSettings", key = "#installationId")
    public Mono<Boolean> addedRepository(Long installationId, List<GHWebhookInstallationRepoPayload.Repository> repositories) {
        log.info("Adding repository {} to user settings for installation ID: {}", repositories, installationId);
        return userSettingRepository.findByGithubInstallationId(installationId)
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .switchIfEmpty(Mono.error(new WebhookMainException(
                        String.format("User settings not found for installationId: %s", installationId),
                        HttpStatus.NOT_FOUND))
                )
                .handle(sinkAddedTriggers(repositories))
                .flatMap(this::saveUserSettings)
                .defaultIfEmpty(false)
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to add repository to user settings for installation ID: %s", installationId),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error));
    }

    /**
     * Saves user settings to MongoDB with automatic retry and timeout.
     * Applies exponential retry configuration (max 3 attempts) and 5-second timeout.
     *
     * @param userSetting UserSetting entity to save
     * @return Mono emitting true if save succeeds
     * @throws MongoDbException if save fails after all retries
     */
    public Mono<Boolean> saveUserSettings(UserSetting userSetting){
        log.info("Saving user setting for installation ID: {}", userSetting.getGithubInstallationId());
        return userSettingRepository.save(userSetting)
                .timeout(Duration.ofSeconds(5))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .doOnSuccess(saved -> log.info("User setting saved successfully for installation ID: {}", userSetting.getGithubInstallationId()))
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to save user setting for installation ID: %s", userSetting.getGithubInstallationId()),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error))
                .map(e -> true);
    }

    /**
     * Deletes user settings from MongoDB for a specific installation and userId.
     * Called when receiving INSTALLATION event with DELETED action.
     * Applies automatic retry and 5-second timeout.
     *
     * @param installationId GitHub App installation ID
     * @param userId GitHub user ID
     * @return Mono emitting true if deletion succeeds (even if no document found)
     * @throws MongoDbException if deletion fails after all retries
     */
    @CacheEvict(value = "userSettings", key = "#installationId")
    public Mono<Boolean> deleteUserSettings(Long installationId, Long userId) {
        log.info("Deleting user settings for installation ID: {} and userId: {}", installationId, userId);
        return userSettingRepository.deleteByInstallationIdAndUserId(installationId,userId)
                .timeout(Duration.ofSeconds(5))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .doOnSuccess(deleted -> {
                    if(deleted != null && deleted > 0)
                        log.info("User settings deleted successfully");
                    else
                        log.info("No user settings found to delete");
                })
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to delete user settings for installation ID: %s and userId: %s", installationId, userId),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error))
                .map(e -> true);
    }

    /**
     * Updates user settings by merging with existing data.
     * If user exists, overwrites globalSettings and repositories while keeping createdAt.
     * If not exists, creates new document with current timestamp.
     * Automatically invalidates cache to force reload on next access.
     *
     * @param settings DTO with new settings to save
     * @return Mono emitting true if update succeeds
     * @throws MongoDbException if operation fails after all retries
     */
    @CacheEvict(value = "userSettings", key = "#settings.githubInstallationId")
    public Mono<Boolean> updateUserSettings(UserSettingDto settings) {
        log.info("Saving user settings for githubInstallationId: {}", settings.getGithubInstallationId());
        return Mono.defer(() -> {
            UserSetting settingEntity = userSettingMapper.settingToEntity(settings);
            LocalDateTime now = LocalDateTime.now();
            if (settingEntity.getId() == null) {
                settingEntity.setCreatedAt(now);
            }
            settingEntity.setUpdatedAt(now);
            return Mono.just(settingEntity);
        })
        .zipWhen(userSetting ->
                userSettingRepository.findByGithubInstallationId(userSetting.getGithubInstallationId())
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
        )
        .flatMap(tuple -> mergeSettings(tuple.getT1(),tuple.getT2()))
        .flatMap(userSetting ->
                userSettingRepository.save(userSetting)
                .timeout(Duration.ofSeconds(5))
                .transformDeferred(RetryOperator.of(mongoRetry))
        )
        .doOnSuccess(saved -> log.info("User settings saved successfully for githubInstallationId: {}", settings.getGithubInstallationId()))
        .onErrorMap(error -> new MongoDbException(
            String.format("Failed to save user settings for githubInstallationId: %s", settings.getGithubInstallationId()),
            HttpStatus.INTERNAL_SERVER_ERROR,
            error))
        .map(e -> true);
    }

    /**
     * Retrieves user settings for userId with automatic caching.
     * Result is cached for 50 minutes (configured in CacheConfig).
     * Called on every PR event to check if analysis is enabled for the repository.
     * Applies automatic retry (max 3 attempts) and 3-second timeout.
     *
     * @param installationId GitHub installation ID
     * @return Mono emitting UserSettingDto if found, empty if not exists
     * @throws MongoDbException if retrieval fails after all retries
     */
    @Cacheable(value = "userSettings", key = "#installationId")
    public Mono<UserSettingDto> getUserSettings(Long installationId) {
        log.info("Retrieving user settings for installationId: {}", installationId);
        return userSettingRepository.findByGithubInstallationId(installationId)
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .switchIfEmpty(Mono.error(new WebhookMainException(
                    String.format("User settings not found for installationId: %s", installationId),
                    HttpStatus.NOT_FOUND))
                )
                .map(userSettingMapper::settingToDto)
                .doOnNext(settings -> log.info("User settings retrieved successfully for installationId: {}", installationId))
                .doOnSuccess(settings -> {
                    if (settings == null) {
                        log.info("No user settings found for installationId: {}", installationId);
                    }
                })
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to retrieve user settings for installationId: %s", installationId),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error)
                );
    }

    private static BiConsumer<UserSetting, SynchronousSink<UserSetting>> sinkRemovedTriggers(List<GHWebhookInstallationRepoPayload.Repository> repositories) {
        List<Long> repoIdsToRemove = repositories.stream()
                .map(GHWebhookInstallationRepoPayload.Repository::getId)
                .toList();
        return (userSetting,sink) -> {
            List<RepositoryConfig> repos = new ArrayList<>(userSetting.getRepositories());
            boolean removed = repos.removeIf(repo -> repoIdsToRemove.contains(repo.getRepoId()));
            if (removed) {
                userSetting.setRepositories(repos);
                userSetting.setUpdatedAt(LocalDateTime.now());
                sink.next(userSetting);
                return;
            }
            log.info("No repositories removed - IDs {} not found for installation {}",
                     repoIdsToRemove, userSetting.getGithubInstallationId());
            sink.complete();
        };
    }

    private static BiConsumer<UserSetting, SynchronousSink<UserSetting>> sinkAddedTriggers(List<GHWebhookInstallationRepoPayload.Repository> repositories) {
        return (userSetting,sink) -> {
            List<Long> existingRepoIds = userSetting.getRepositories().stream()
                    .map(RepositoryConfig::getRepoId)
                    .toList();

            List<RepositoryConfig> newRepos = repositories.stream()
                    .filter(repo -> !existingRepoIds.contains(repo.getId()))
                    .map(repo -> RepositoryConfig.builder()
                            .repoId(repo.getId())
                            .repoName(repo.getName())
                            .rules(AnalysisRules.defaults())
                            .triggers(TriggerSettings.builder().build())
                            .isActive(true)
                            .metadata(RepositoryMetadata.builder().build())
                            .notifications(NotificationSettings.builder().build())
                            .build())
                    .toList();

            if(!newRepos.isEmpty()){
                List<RepositoryConfig> updatedRepos = new ArrayList<>(newRepos);
                updatedRepos.addAll(userSetting.getRepositories());
                userSetting.setRepositories(updatedRepos);
                userSetting.setUpdatedAt(LocalDateTime.now());
                sink.next(userSetting);
                return;
            }
            log.info("No new repositories to add - all IDs {} already exist for installation {}",
                     repositories.stream().map(GHWebhookInstallationRepoPayload.Repository::getId).toList(),
                     userSetting.getGithubInstallationId());
            sink.complete();
        };
    }

    private Mono<UserSetting> mergeSettings(UserSetting incoming, UserSetting existing) {
        if(existing != null){
            existing.setGlobalSettings(incoming.getGlobalSettings());
            existing.setRepositories(incoming.getRepositories());
            existing.setUpdatedAt(LocalDateTime.now());

            return Mono.just(existing);
        }
        return Mono.error(new WebhookMainException("User settings not found for update", HttpStatus.NOT_FOUND));
    }
}
