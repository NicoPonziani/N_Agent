package it.np.n_agent.utilities;

import it.np.n_agent.entity.*;
import it.np.n_agent.entity.enums.AccountType;
import it.np.n_agent.github.dto.GHWebhookInstallationPaylaod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserSettingUtility {

    private static final Logger log = LoggerFactory.getLogger(UserSettingUtility.class);

    public static List<RepositoryConfig> buildDefaultRepositories(List<GHWebhookInstallationPaylaod.Repository> repos){
        log.info("Building default repository configurations");
        if(repos != null && !repos.isEmpty()){
            return repos.stream().map(repo -> RepositoryConfig.builder()
                            .repoId(repo.getId())
                            .repoName(repo.getName())
                            .rules(AnalysisRules.defaults())
                            .isActive(true)
                            .metadata(RepositoryMetadata.builder().build())
                            .triggers(TriggerSettings.defaults())
                            .notifications(NotificationSettings.defaults())
                            .build())
                    .toList();
        }
        return List.of();
    }

    public static AccountInfo buildAccountInfo(GHWebhookInstallationPaylaod.Account account){
        log.info("Building account info for account ID: {}",account.getId());
        return AccountInfo.builder()
                .id(account.getId())
                .name(account.getLogin())
                .login(account.getLogin())
                .type(AccountType.USER) // Simplification: assuming USER type
                .build();
    }
}
