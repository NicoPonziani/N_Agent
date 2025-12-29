package it.np.n_agent.entity;

import it.np.n_agent.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    @Field("id")
    private Long id;
    @Field("login")
    private String login; // GitHub username
    @Field("type")
    private AccountType type; // USER o ORGANIZATION
    @Field("email")
    private String email;
    @Field("name")
    private String name; // Full name if available
}