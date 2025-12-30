package it.np.n_agent.service;

import it.np.n_agent.ai.dto.CodeAnalysisResult;
import it.np.n_agent.dto.UserSettingDto;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import it.np.n_agent.github.dto.GHWebhookPrPayload;
import it.np.n_agent.github.enums.ActionType;
import it.np.n_agent.github.enums.EventType;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.function.BiConsumer;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.*;
import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final AiService aiService;
    private final GithubService githubService;
    private final NotificationService notificationService;
    private final UserSettingService userSettingService;

    @Autowired
    public WebhookService(AiService aiService, GithubService githubService, NotificationService notificationService, UserSettingService userSettingService) {
        this.aiService = aiService;
        this.githubService = githubService;
        this.notificationService = notificationService;
        this.userSettingService = userSettingService;
    }

    public Mono<Boolean> processGithubWebhook(Object payload, String eventType) {
        return switch (EventType.fromValue(eventType)) {
            case PUSH -> handlePushEvent((GHWebhookPrPayload) payload);
            case PULL_REQUEST -> handlePullRequestEvent((GHWebhookPrPayload) payload);
            case INSTALLATION -> handleInstallationEvent((GHWebhookInstallationPaylaod) payload);
            default -> Mono.error(new WebhookMainException("Unsupported event type", HttpStatus.BAD_REQUEST));
        };
    }

    private Mono<Boolean> handleInstallationEvent(GHWebhookInstallationPaylaod payload) {
        log.info("Processing Installation event for installation ID {} action {}", payload.getInstallation().getId(),payload.getAction());
        if(ActionType.CREATED.name().equalsIgnoreCase(payload.getAction()))
            return userSettingService.buildUserSetting(payload)
                    .flatMap(userSettingService::saveUserSettings);
        else if (ActionType.DELETED.name().equalsIgnoreCase(payload.getAction()))
            return userSettingService.deleteUserSettings(payload.getInstallation().getId(),payload.getSender().getId());

        log.info("No action taken for installation event with action: {}", payload.getAction());
        return Mono.just(true);
    }

    private Mono<Boolean> handlePullRequestEvent(GHWebhookPrPayload payload) {
        log.info("Processing Pull Request event for PR #{}", payload.getPullRequest().getNumber());

        final Long installationId = payload.getInstallation().getId();
        final Long prNumber = payload.getPullRequest().getNumber();
        final String apiPath = payload.getPullRequest().getUrl();
        final String commitSha = payload.getPullRequest().getHead().getSha();
        final String owner = payload.getPullRequest().getUser().getLogin();
        final String repo = payload.getRepository().getName();
        final Long userId = payload.getSender().getId();

        WebhookBaseInfo baseInfo =
                WebhookBaseInfo.builder()
                .installationId(installationId)
                .commitSha(commitSha)
                .url(apiPath)
                .prNumber(prNumber)
                .owner(owner)
                .repo(repo)
                .build();

        return userSettingService.getUserSettings(userId)
                            .handle(sinkActionTriggers(payload))
                            .zipWhen(
                                    setting -> githubService.retrieveDiff(apiPath,installationId),
                                    (setting, diff) -> buildWebhookZipInput(diff, setting, repo,baseInfo)
                            )
                            .zipWhen(
                                    input -> aiService.analyzeDiff(input.diff(),input.rules()),
                                    WebhookZipInput::withAnalysisResult
                            )
                            .delayUntil((input) -> aiService.handleAiResponse(input.analysisResult, prNumber, installationId))
                            .flatMap(input -> notificationService.sendNotification(input.webhookBaseInfo,input.analysisResult,input.notificationSettingsDto))
                            .defaultIfEmpty(false)
                            .subscribeOn(Schedulers.boundedElastic());
    }


    private Mono<Boolean> handlePushEvent(GHWebhookPrPayload payload) {
        log.info("Push event not implemented yet");
        return Mono.just(false);
    }

    private static BiConsumer<UserSettingDto, SynchronousSink<UserSettingDto>> sinkActionTriggers(GHWebhookPrPayload payload) {
        return (setting, sink) -> {
            String repo = payload.getRepository().getName();
            Optional<UserSettingDto.RepositoryConfigDto> repoConfigOpt =
                    setting.getRepositories().stream()
                            .filter(r -> r.getRepoName().equalsIgnoreCase(repo))
                            .findFirst();
            if (repoConfigOpt.isPresent() && repoConfigOpt.get().getIsActive()) {
                TriggerSettingsDto conf = repoConfigOpt.get().getTriggers();
                if (ActionType.isValidActionPR(conf, payload.getAction()))
                    sink.next(setting);
            }
            log.info("Repository {} is inactive for action {}. Skipping analysis.", repo, payload.getAction());
        };
    }

    public static WebhookZipInput buildWebhookZipInput(String diff, UserSettingDto setting, String repoName, WebhookBaseInfo baseInfo){
        RepositoryConfigDto repoConfig =
                setting.getRepositories().stream()
                        .filter(r -> r.getRepoName().equalsIgnoreCase(repoName))
                        .findFirst().orElse(RepositoryConfigDto.builder().build());

        AnalysisRulesDto rules = repoConfig.getRules();

        return WebhookZipInput.builder()
                .diff(diff)
                .repoConfig(repoConfig)
                .rules(rules)
                .webhookBaseInfo(baseInfo)
                .notificationSettingsDto(repoConfig.getNotifications())
                .build();
    }

    @Builder
    public record WebhookZipInput(String diff,
                                  RepositoryConfigDto repoConfig,
                                  AnalysisRulesDto rules,
                                  NotificationSettingsDto notificationSettingsDto,
                                  WebhookBaseInfo webhookBaseInfo,
                                  CodeAnalysisResult analysisResult
    ){
        WebhookZipInput withProcessResult(WebhookBaseInfo result){
            return WebhookZipInput.builder()
                    .diff(this.diff)
                    .repoConfig(this.repoConfig)
                    .rules(this.rules)
                    .notificationSettingsDto(this.notificationSettingsDto)
                    .analysisResult(this.analysisResult)
                    .webhookBaseInfo(result)
                    .build();
        }

        WebhookZipInput withAnalysisResult(CodeAnalysisResult analysisResult){
            return WebhookZipInput.builder()
                    .diff(this.diff)
                    .repoConfig(this.repoConfig)
                    .rules(this.rules)
                    .notificationSettingsDto(this.notificationSettingsDto)
                    .webhookBaseInfo(this.webhookBaseInfo)
                    .analysisResult(analysisResult)
                    .build();
        }
    }

    @Builder
    public record WebhookBaseInfo(Long installationId,
                                  String commitSha,
                                  String url,
                                  Long prNumber,
                                  String owner,
                                  String repo

    ) {}
}
