package com.gitprism.GitPRism.gitpullrequests.controller;

import com.gitprism.GitPRism.gitpullrequests.dto.GitHubPrResponseWrapper;
import com.gitprism.GitPRism.gitpullrequests.service.GitHubPrService;
import com.gitprism.GitPRism.gitrepositorys.dto.response.GitHubRepoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/pulls")
@Tag(name = "PR 조회 API", description = "PR의 ID, 제목, url, 내용을 조회 + 저장")
public class GitHubPrController {

    private final GitHubPrService prService;

    public GitHubPrController(GitHubPrService prService) {
        this.prService = prService;
    }

    // 변경된 GET 방식 컨트롤러
    @GetMapping
    public GitHubPrResponseWrapper getPullRequests(Authentication authentication) {
        String githubId = authentication.getName(); // ✅ JWT에서 GitHub ID 추출
        return prService.getParticipatedRepositoriesByGithubId(githubId);
    }

}
