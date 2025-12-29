package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRules {

    // Feature toggles
    @Builder.Default
    @Field("detect_todos")
    private Boolean detectTODOs = true;

    @Builder.Default
    @Field("predict_regret")
    private Boolean predictRegret = true;

    @Builder.Default
    @Field("check_complexity")
    private Boolean checkComplexity = true;

    @Builder.Default
    @Field("detect_duplication")
    private Boolean detectDuplication = false;

    @Builder.Default
    @Field("check_test_coverage")
    private Boolean checkTestCoverage = false;

    // Ignore patterns
    @Builder.Default
    @Field("ignore_patterns")
    private List<String> ignorePatterns = new ArrayList<>(List.of(
            "*.test.js",
            "*.spec.ts",
            "*.generated.*",
            "node_modules/**",
            "build/**",
            "dist/**"
    ));

    @Field("languages")
    private List<String> languages; // null = all languages

    // Factory method x defaults
    public static AnalysisRules defaults() {
        return AnalysisRules.builder().build();
    }
}
