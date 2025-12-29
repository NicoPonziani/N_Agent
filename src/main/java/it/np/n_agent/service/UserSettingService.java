package it.np.n_agent.service;

import it.np.n_agent.dto.UserSettingDto;
import it.np.n_agent.entity.GlobalSettings;
import it.np.n_agent.entity.UserSetting;
import it.np.n_agent.exception.MongoDbException;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import it.np.n_agent.mapper.UserSettingMapper;
import it.np.n_agent.repository.UserSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static it.np.n_agent.utilities.UserSettingUtility.buildAccountInfo;
import static it.np.n_agent.utilities.UserSettingUtility.buildDefaultRepositories;

@Service
public class UserSettingService {

    private static final Logger log = LoggerFactory.getLogger(UserSettingService.class);

    private final UserSettingRepository userSettingRepository;
    private final UserSettingMapper userSettingMapper;

    @Autowired
    public UserSettingService(UserSettingRepository userSettingRepository, UserSettingMapper userSettingMapper) {
        this.userSettingRepository = userSettingRepository;
        this.userSettingMapper = userSettingMapper;
    }

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

    public Mono<Boolean> saveUserSettings(UserSetting userSetting){
        log.info("Saving user setting for installation ID: {}", userSetting.getGithubInstallationId());
        return userSettingRepository.save(userSetting)
                .doOnSuccess(saved -> log.info("User setting saved successfully for installation ID: {}", userSetting.getGithubInstallationId()))
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to save user setting for installation ID: %s", userSetting.getGithubInstallationId()),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error))
                .map(e -> true);
    }

    public Mono<Boolean> deleteUserSettings(Long installationId, Long userId) {
        log.info("Deleting user settings for installation ID: {} and userId: {}", installationId, userId);
        return userSettingRepository.deleteByInstallationIdAndUserId(installationId,userId)
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

    public Mono<Boolean> updateUserSettings(UserSettingDto settings) {
        log.info("Saving user settings for userId: {}", settings.getUserId());
        return Mono.defer(() -> {
            UserSetting settingEntity = userSettingMapper.settingToEntity(settings);
            LocalDateTime now = LocalDateTime.now();
            if (settingEntity.getId() == null) {
                settingEntity.setCreatedAt(now);
            }
            settingEntity.setUpdatedAt(now);
            return Mono.just(settingEntity);
        })
        .zipWhen(userSetting -> userSettingRepository.findByUserId(userSetting.getUserId()))
        .flatMap(tuple -> mergeSettings(tuple.getT1(),tuple.getT2()))
        .flatMap(userSettingRepository::save)
        .doOnSuccess(saved -> log.info("User settings saved successfully for userId: {}", settings.getUserId()))
        .onErrorMap(error -> new MongoDbException(
            String.format("Failed to save user settings for userId: %s", settings.getUserId()),
            HttpStatus.INTERNAL_SERVER_ERROR,
            error))
        .map(e -> true);
    }

    public Mono<UserSettingDto> getUserSettings(Long userId) {
        log.info("Retrieving user settings for userId: {}", userId);
        return userSettingRepository.findByUserId(userId)
                .map(userSettingMapper::settingToDto)
                .doOnNext(settings -> log.info("User settings retrieved successfully for userId: {}", userId))
                .doOnSuccess(settings -> {
                    if (settings == null) {
                        log.info("No user settings found for userId: {}", userId);
                    }
                })
                .onErrorMap(error -> new MongoDbException(
                    String.format("Failed to retrieve user settings for userId: %s", userId),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    error)
                );
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
