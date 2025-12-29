package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryMetadata {

    @Field("default_branch")
    private String defaultBranch;
    @Field("primary_language")
    private String primaryLanguage;
    @Field("is_private")
    private Boolean isPrivate;
    @Field("last_analyzed_at")
    private LocalDateTime lastAnalyzedAt;

    @Builder.Default
    @Field("total_analyses_count")
    private Integer totalAnalysesCount = 0;
}