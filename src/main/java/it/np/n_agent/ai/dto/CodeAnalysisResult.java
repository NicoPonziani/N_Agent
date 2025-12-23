package it.np.n_agent.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import it.np.n_agent.ai.enums.RecommendationEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CodeAnalysisResult {

    @JsonPropertyDescription("Brief overview of the analysis")
    private String summary;

    @JsonPropertyDescription("List of identified code issues")
    private List<CodeIssue> issues = new ArrayList<>();

    @JsonProperty("regretProbability")
    @JsonPropertyDescription("Probability of future regret (0.0 to 1.0)")
    private Double regretProbability;

    @JsonPropertyDescription("Review recommendation: APPROVE, REQUEST_CHANGES, or COMMENT")
    private RecommendationEnum recommendation;

    @JsonPropertyDescription("Estimated time to fix all issues (in hours)")
    private Double estimatedFixTime;

    @JsonPropertyDescription("Analysis timestamp")
    private LocalDateTime analyzedAt = LocalDateTime.now();

    // Helper methods
    public int getIssuesCount() {
        return issues != null ? issues.size() : 0;
    }

    public long getCriticalIssuesCount() {
        return issues.stream()
                .filter(i -> "CRITICAL".equalsIgnoreCase(i.getSeverity()))
                .count();
    }

    public boolean hasBlockingIssues() {
        return getCriticalIssuesCount() > 0;
    }
}
