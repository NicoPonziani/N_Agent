package it.np.n_agent.github.enums;

import lombok.Getter;

@Getter
public enum HeaderGithubUtility {
    APPLICATION_VND_V3_JSON("application/vnd.github.v3+json"),
    APPLICATION_VND_V3_DIFF("application/vnd.github.v3.diff"),
    APPLICATION_VND_JSON("application/vnd.github+json")
    ;

    private final String headerValue;

    HeaderGithubUtility(String headerValue) {
        this.headerValue = headerValue;
    }
}
