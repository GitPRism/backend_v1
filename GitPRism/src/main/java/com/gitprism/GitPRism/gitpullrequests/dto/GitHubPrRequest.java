package com.gitprism.GitPRism.gitpullrequests.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubPrRequest {
    private String owner;
    private String repo;
    private String accessToken;

}

