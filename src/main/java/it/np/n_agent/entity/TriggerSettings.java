package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerSettings {

    // Eventi GitHub
    @Builder.Default
    @Field("on_pr_open")
    private Boolean onPROpen = true;

    @Builder.Default
    @Field("on_pr_update")
    private Boolean onPRUpdate = true;

    @Builder.Default
    @Field("on_pr_reopen")
    private Boolean onPRReopen = false;

    @Builder.Default
    @Field("on_push")
    private Boolean onPush = false;

    public static TriggerSettings defaults() {
        return TriggerSettings.builder().build();
    }
}