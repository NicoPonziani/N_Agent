package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryConfig {

    // Identity
    @Indexed
    @Field("repo_id")
    private Long repoId; // GitHub repository ID (immutabile)

    @Indexed
    @Field("repo_name")
    private String repoName; // Full name: "owner/repo"

    @Builder.Default
    @Field("is_active")
    private Boolean isActive = true;

    // Regole di analisi
    @Builder.Default
    @Field("rules")
    private AnalysisRules rules = new AnalysisRules();

    // Quando triggerare analisi
    @Builder.Default
    @Field("triggers")
    private TriggerSettings triggers = new TriggerSettings();

    // Dove notificare
    @Builder.Default
    @Field("notifications")
    private NotificationSettings notifications = new NotificationSettings();

    // Metadata cached
    @Builder.Default
    @Field("metadata")
    private RepositoryMetadata metadata = new RepositoryMetadata();
}