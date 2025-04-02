package com.gitprism.GitPRism.repositorys.controller;

import com.gitprism.GitPRism.repositorys.dto.response.GitHubRepoResponse;
import com.gitprism.GitPRism.repositorys.service.GitHubRepoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github-repos")
@RequiredArgsConstructor
public class GitHubRepoController {

    private final GitHubRepoService gitHubRepoService;

    @Operation(
            summary = "내가 참여한 GitHub 레포지토리 조회",
            description = "DB에 저장된 accessToken으로 GitHub API를 호출하여 사용자가 PR 또는 이슈에 참여한 레포지토리 목록을 가져옵니다."
    )
    @GetMapping
    public ResponseEntity<?> getParticipatedRepos(@RequestParam("userId") Long userId) {
        // ✅ 새 방식으로 참여한 레포만 가져오기
        List<GitHubRepoResponse> responseList = gitHubRepoService.getParticipatedRepositories(userId);
        return ResponseEntity.ok(responseList);
    }
}

