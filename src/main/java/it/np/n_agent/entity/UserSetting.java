package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "user_setting")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("user_id")
    private Long userId;

    @Indexed(unique = true)
    @Field("github_installation_id")
    private Long githubInstallationId;

    // Account info cached da GitHub
    @Field("account_info")
    private AccountInfo account;

    // Lista repository configurati
    @Builder.Default
    @Field("repositories")
    private List<RepositoryConfig> repositories = new ArrayList<>();

    // Settings globali per l'installazione
    @Builder.Default
    @Field("global_settings")
    private GlobalSettings globalSettings = new GlobalSettings();

    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("last_login_at")
    private LocalDateTime lastLoginAt;
}