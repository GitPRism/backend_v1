package com.gitprism.GitPRism.Repository.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubTokenRequest {
    private Long userId; // accessToken은 제거
}