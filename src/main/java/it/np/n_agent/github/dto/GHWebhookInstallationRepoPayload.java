package it.np.n_agent.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GHWebhookInstallationRepoPayload {
    private String action;
    private Installation installation;
    @JsonProperty("repositories_removed")
    private List<Repository> repositoriesRemoved;
    @JsonProperty("repositories_added")
    private List<Repository> repositoriesAdded;
    private Sender sender;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Installation {
        private Long id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        private String login;
        private Long id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private Long id;
        private String name;
        @JsonProperty("full_name")
        private String fullName;
        @JsonProperty("private")
        private Boolean _private;
    }
}
