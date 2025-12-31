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

    /**
     * Main entry point for processing GitHub webhook events.
     * Routes the event to the appropriate handler based on event type.
     *
     * @param payload webhook payload object (can be GHWebhookPrPayload or GHWebhookInstallationPayload)
     * @param eventType type of GitHub event (PUSH, PULL_REQUEST, INSTALLATION)
     * @return Mono emitting true if processing succeeds, false otherwise
     * @throws WebhookMainException if the event type is not supported
     */
    public Mono<Boolean> processGithubWebhook(Object payload, String eventType) {
        return switch (EventType.fromValue(eventType)) {
            case PUSH -> handlePushEvent((GHWebhookPrPayload) payload);
            case PULL_REQUEST -> handlePullRequestEvent((GHWebhookPrPayload) payload);
            case INSTALLATION -> handleInstallationEvent((GHWebhookInstallationPaylaod) payload);
            default -> Mono.error(new WebhookMainException("Unsupported event type", HttpStatus.BAD_REQUEST));
        };
    }

    /**
     * Handles GitHub App INSTALLATION events (created/deleted).
     * - CREATED action: builds and saves default user settings
     * - DELETED action: removes user settings from database
     *
     * @param payload GitHub installation webhook payload containing installation details
     * @return Mono emitting true if the operation succeeds
     */
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

    /**
     * Handles GitHub PULL_REQUEST events (opened/updated/reopened).
     * Orchestrates the complete analysis workflow:
     * 1. Retrieves user settings and checks if repository triggers are active
     * 2. Fetches PR diff from GitHub API
     * 3. Sends diff to AI service for analysis
     * 4. Handles AI response (stores results, posts comments)
     * 5. Sends notifications based on user preferences
     *
     * Uses reactive chain with bounded elastic scheduler for potentially blocking operations.
     * Returns empty (false) if repository is inactive or triggers don't match.
     *
     * @param payload GitHub PR webhook payload containing PR details and metadata
     * @return Mono emitting true if analysis completes successfully, false if skipped
     */
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


    /**
     * Handles GitHub PUSH events.
     * Currently not implemented - returns false.
     *
     * @param payload GitHub push webhook payload
     * @return Mono emitting false (not implemented)
     */
    private Mono<Boolean> handlePushEvent(GHWebhookPrPayload payload) {
        log.info("Push event not implemented yet");
        return Mono.just(false);
    }

    /**
     * Creates a BiConsumer for filtering user settings based on repository triggers.
     * Used in reactive .handle() operator to conditionally emit settings.
     *
     * Emits the setting downstream only if:
     * - Repository is found in user settings
     * - Repository is active (isActive = true)
     * - Action trigger matches the PR action (onPROpen, onPRUpdate, etc.)
     *
     * If conditions are not met, completes the sink without emitting to prevent reactive chain hang.
     *
     * @param payload GitHub PR webhook payload to extract repository name and action
     * @return BiConsumer that emits setting if triggers match, completes otherwise
     */
    private static BiConsumer<UserSettingDto, SynchronousSink<UserSettingDto>> sinkActionTriggers(GHWebhookPrPayload payload) {
        return (setting, sink) -> {
            String repo = payload.getRepository().getName();
            Optional<UserSettingDto.RepositoryConfigDto> repoConfigOpt =
                    setting.getRepositories().stream()
                            .filter(r -> r.getRepoName().equalsIgnoreCase(repo))
                            .findFirst();
            if (repoConfigOpt.isPresent() && repoConfigOpt.get().getIsActive()) {
                TriggerSettingsDto conf = repoConfigOpt.get().getTriggers();
                if (ActionType.isValidActionPR(conf, payload.getAction())) {
                    sink.next(setting);
                    return;
                }
            }
            log.info("Repository {} is inactive for action {}. Skipping analysis.", repo, payload.getAction());
            sink.complete();
        };
    }

    /**
     * Builds a WebhookZipInput record combining all data needed for analysis workflow.
     * Extracts repository-specific configuration (rules, notifications) from user settings.
     * If repository is not found in settings, creates a default empty configuration.
     *
     * @param diff Git diff content retrieved from GitHub API
     * @param setting User settings containing repository configurations
     * @param repoName Repository name to extract configuration for
     * @param baseInfo Base webhook information (installationId, PR number, etc.)
     * @return WebhookZipInput record with all data for downstream processing
     */
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

    /**
     * Immutable record holding base webhook metadata extracted from GitHub payload.
     * Contains essential information needed for GitHub API calls and notifications.
     *
     * @param installationId GitHub App installation ID for authentication
     * @param commitSha Git commit SHA of the PR head
     * @param url GitHub API URL for the pull request
     * @param prNumber Pull request number
     * @param owner Repository owner (user or organization login)
     * @param repo Repository name
     */
    @Builder
    public record WebhookBaseInfo(Long installationId,
                                  String commitSha,
                                  String url,
                                  Long prNumber,
                                  String owner,
                                  String repo

    ) {}
}
