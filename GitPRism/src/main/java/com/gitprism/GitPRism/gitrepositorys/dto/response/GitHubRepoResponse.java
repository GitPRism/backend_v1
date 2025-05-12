package com.gitprism.GitPRism.gitrepositorys.dto.response;

import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GitHubRepoResponse {
    private Long id;
    private String githubId;
    private String repoName;
    private String url;
    private String description;
    private String defaultBranch;
    private String language;
    private String visibility;
    private String orgAvatarUrl;

    public static List<GitHubRepoResponse> fromEntities(List<Repo> repos) {
        return repos.stream().map(repo ->
                GitHubRepoResponse.builder()
                        .id(repo.getId())
                        .githubId(repo.getGithubId())
                        .repoName(repo.getRepoName())
                        .url(repo.getUrl())
                        .description(repo.getDescription())
                        .defaultBranch(repo.getDefaultBranch())
                        .language(repo.getLanguage())
                        .visibility(repo.getVisibility())
                        .orgAvatarUrl(repo.getOrgAvatarUrl())
                        .build()
        ).collect(Collectors.toList());
    }
}
