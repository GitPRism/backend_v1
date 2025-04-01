package com.gitprism.GitPRism.Repository.controller;

import com.gitprism.GitPRism.Repository.dto.request.GitHubTokenRequest;
import com.gitprism.GitPRism.Repository.dto.response.GitHubRepoResponse;
import com.gitprism.GitPRism.Repository.service.GitHubRepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/github-repos")
@RequiredArgsConstructor
public class GitHubRepoController {

    private final GitHubRepoService gitHubRepoService;

    @PostMapping
    public ResponseEntity<GitHubRepoResponse> getRepositories(@RequestBody GitHubTokenRequest request) {
        // accessToken을 DB에서 조회
        String accessToken = gitHubRepoService.getAccessTokenByUserId(request.getUserId());

        // GitHub API 호출 및 결과 반환
        GitHubRepoResponse response = gitHubRepoService.getRepositoriesFromGitHub(accessToken);
        return ResponseEntity.ok(response);
    }
}
