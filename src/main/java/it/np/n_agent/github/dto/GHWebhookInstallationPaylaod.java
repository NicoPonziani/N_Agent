package it.np.n_agent.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GHWebhookInstallationPaylaod {
    private String action;  // "opened", "reopened", "synchronize"

    private Installation installation;

    private List<Repository> repositories;
    private Sender sender;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Installation {
        private Long id;
        private Account account;
        @JsonProperty("repository_selection")
        private String repositorySelection;
        @JsonProperty("repositories_url")
        private String repositoriesUrl;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        private String login;
        private Long id;
        private String type;
        @JsonProperty("user-view-type")
        private String userViewType;
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
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        private String login;
        private Long id;
    }

}
