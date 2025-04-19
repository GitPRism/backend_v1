package com.gitprism.GitPRism.portfolios.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPrResponse {
    private int number;
    private String title;
    private String body;
    private User user;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String login;
    }
}
