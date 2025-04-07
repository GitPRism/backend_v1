package com.gitprism.GitPRism.gitpullrequests.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubPrResponseWrapper {
    private String message;
    private int code;
    private Long id;
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String git_id;
        private List<GitHubPrResponse> pull_requests;
    }
}
