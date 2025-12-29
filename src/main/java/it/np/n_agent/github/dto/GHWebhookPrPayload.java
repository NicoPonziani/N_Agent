package it.np.n_agent.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GHWebhookPrPayload {

    private String action;  // "opened", "reopened", "synchronize"

    @JsonProperty("pull_request")
    private PullRequest pullRequest;
    private Repository repository;
    private Installation installation;
    private Integer number;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private Long number;
        private String title;
        private String url;

        @JsonProperty("diff_url")
        private String diffUrl;  // Per scaricare il diff

        @JsonProperty("html_url")
        private String htmlUrl;  // Link alla PR

        private User user;
        private Head head;  // Branch sorgente
        private Base base;  // Branch target

        // Statistiche utili
        private Integer commits;
        private Integer additions;
        private Integer deletions;
        @JsonProperty("changed_files")
        private Integer changedFiles;
        @JsonProperty("review_comments_url")
        private String reviewCommentsUrl;
        @JsonProperty("comments_url")
        private String commentsUrl;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String login;  // Username GitHub
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Head {
        private String ref;  // Nome branch (es: "test_hook")
        private String sha;  // Commit SHA
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Base {
        private String ref;  // Nome branch target (es: "main")
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        @JsonProperty("full_name")
        private String fullName;  // "NicoPonziani/SecurityDemo"

        private String name;  // "SecurityDemo"

        @JsonProperty("private")
        private Boolean isPrivate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Installation {
        private Long id;  // Per identificare l'utente univocamente
    }
}
