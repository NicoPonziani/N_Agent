package it.np.n_agent.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "historical_issue")
public class HistoricalIssueEntity {

    @Id
    private String id;

    @Field("repository")
    private String repository;

    @Field("pr_number")
    private Long prNumber;

    @Field("type")
    private String type;

    @Field("resolution")
    private String resolution;

    @Field("time_to_fix")
    private Double timeToFix;

    @Field("found_at")
    private LocalDateTime foundAt;

    @Field("user_installation_id")
    private Long userInstallationId;
}
