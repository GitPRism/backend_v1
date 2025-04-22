package com.gitprism.GitPRism.gitrepositorys.controller;

import com.gitprism.GitPRism.gitrepositorys.dto.response.GitHubRepoResponse;
import com.gitprism.GitPRism.gitrepositorys.service.GitHubRepoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github-repos")
@RequiredArgsConstructor
@Tag(name = "Repository 조회 API", description = "사용자가 활동한(commit 기준) 레포를 조회")

public class GitHubRepoController {

    private final GitHubRepoService gitHubRepoService;

    @Operation(
            summary = "내가 참여한 GitHub 레포지토리 조회",
            description = "JWT 인증된 GitHub ID를 기반으로 참여한 레포지토리를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<?> getParticipatedRepos(Authentication authentication) {
        String githubId = authentication.getName(); // JWT의 sub로부터 GitHub ID 추출
        List<GitHubRepoResponse> responseList = gitHubRepoService.getParticipatedRepositoriesByGithubId(githubId);
        return ResponseEntity.ok(responseList);
    }
}
