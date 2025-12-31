package it.np.n_agent.dto;

import it.np.n_agent.entity.enums.AccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingDto {

    @NotNull(message = "userId cannot be null")
    private Long userId;
    @NotNull(message = "githubInstallationId cannot be null")
    private Long githubInstallationId;
    @Valid
    private AccountInfoDto account;
    @Valid
    private List<RepositoryConfigDto> repositories;
    @Valid
    private GlobalSettingsDto globalSettings;

    @Data
    @Builder
    public static class AccountInfoDto {
        private String login;
        private AccountType type;
        private String email;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryConfigDto {

        @NotBlank(message = "repoId cannot be blank")
        private String repoId;

        @NotBlank(message = "repoName cannot be blank")
        private String repoName;

        private Boolean isActive;

        @Valid
        private AnalysisRulesDto rules;

        @Valid
        private TriggerSettingsDto triggers;

        @Valid
        private NotificationSettingsDto notifications;

        @Data
        @Builder
        public static class AnalysisRulesDto {
            private Boolean nullPointerPrediction;
            private Boolean debtEstimation;
            private Boolean detectTODOs;
            private Boolean predictRegret;
            private Boolean checkComplexity;
            private Boolean detectDuplication;
            private Boolean checkTestCoverage;
            private List<String> languages;
        }

        @Data
        @Builder
        public static class TriggerSettingsDto {
            private Boolean onPROpen;
            private Boolean onPRUpdate;
            private Boolean onPRReopen;
            private Boolean onPush;
        }

        @Data
        @Builder
        public static class NotificationSettingsDto {
            private Boolean githubComments;
            private Boolean emailDigestEnabled;
            private String emailDigestFrequency;
            private List<String> emailDigestRecipients;
        }
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalSettingsDto {
        private String aiModel;
        private String language;
        private String timezone;
    }
}
