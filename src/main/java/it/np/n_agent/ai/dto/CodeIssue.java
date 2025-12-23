package it.np.n_agent.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class CodeIssue {

    @JsonPropertyDescription("Issue type: TODO, COMPLEXITY, SECURITY, PERFORMANCE, DEBT")
    private String type;

    @JsonPropertyDescription("Severity: LOW, MEDIUM, HIGH, CRITICAL")
    private String severity;

    @JsonPropertyDescription("File path where issue was found")
    private String file;

    @JsonPropertyDescription("Line number in the file")
    private Integer line;

    @JsonPropertyDescription("Description of the issue")
    private String message;

    @JsonPropertyDescription("Suggested fix or improvement")
    private String suggestion;

    @JsonPropertyDescription("Code snippet with the issue")
    private String codeSnippet;

    @JsonPropertyDescription("Estimated time to fix (in hours)")
    private Double estimatedFixTime;
}
