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
public class GlobalSettings {

    @Builder.Default
    @Field("ai_model")
    private String aiModel = "gpt-4o-mini";

    @Builder.Default
    @Field("language")
    private String language = "EN";

    @Builder.Default
    @Field("timezone")
    private String timezone = "Europe/London";
}
