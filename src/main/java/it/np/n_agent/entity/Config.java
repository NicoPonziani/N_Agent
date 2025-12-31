package it.np.n_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("config")
public class Config {
    @Id
    private String id;
    @Indexed(unique = true)
    @Field("config_id")
    private String configId;
    @Field("ai_models_available")
    private List<String> aiModelsAvailable;
    @Field("languages_available")
    private List<String> languagesAvailable;
}
