package it.np.n_agent.utilities;

public class CommentsUtility {

    public static final String COMMENT_BODY_TEMPLATE =
"""
%s **%s** - %s

**Issue:** %s

%s
""";

    public static final String GENERAL_COMMENT_BODY_TEMPLATE =
"""
## 🤖 AI Code Review

**Summary**: %s
**Recommendation**: %s
**Regret Probability**: %.1f%%
**Issues Found**: %d

See inline comments in Files Changed tab for details.
""";

    public static String getEmojiSeverity(String severity){
        return switch (severity.toUpperCase()) {
            case "CRITICAL" -> "🔴";
            case "HIGH" -> "🟠";
            case "MEDIUM" -> "🟡";
            default -> "🔵";
        };
    }

    public static String formatCommentBody(String severity, String type, String message, String suggestion){
        String severityEmoji = getEmojiSeverity(severity);
        String suggestionText = suggestion != null ? "**Suggestion:** " + suggestion : "";

        return String.format(COMMENT_BODY_TEMPLATE,
                             severityEmoji,
                             severity.toUpperCase(),
                             type,
                             message,
                             suggestionText);
    }

    public static String formatGeneralCommentBody(String summary,String recommendation,double regretProbability, int issuesCount){
        return String.format(GENERAL_COMMENT_BODY_TEMPLATE,
                summary,
                recommendation,
                regretProbability,
                issuesCount
        );
    }
}
