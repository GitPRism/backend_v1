package com.gitprism.GitPRism.gitpullrequests.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubPrResponse {
    private Long id;
    private String PR_title;
    private String PR_details;
    private String author;
    private String htmlUrl;
}

