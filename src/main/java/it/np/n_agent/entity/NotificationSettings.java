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
public class NotificationSettings {

    // GitHub
    @Builder.Default
    @Field("github_comments")
    private Boolean githubComments = true;

    @Builder.Default
    @Field("email_digest_enabled")
    private Boolean emailDigestEnabled = false;

    @Builder.Default
    @Field("email_digest_frequency")
    private String emailDigestFrequency = "weekly";

    @Builder.Default
    @Field("email_digest_recipients")
    private List<String> emailDigestRecipients = new ArrayList<>();

    public static NotificationSettings defaults() {
        return NotificationSettings.builder().build();
    }
}
