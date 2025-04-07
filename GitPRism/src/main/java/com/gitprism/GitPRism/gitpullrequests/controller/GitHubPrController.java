package com.gitprism.GitPRism.gitpullrequests.controller;

import com.gitprism.GitPRism.gitpullrequests.dto.GitHubPrResponseWrapper;
import com.gitprism.GitPRism.gitpullrequests.service.GitHubPrService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/github/pulls")
public class GitHubPrController {

    private final GitHubPrService prService;

    public GitHubPrController(GitHubPrService prService) {
        this.prService = prService;
    }

    // 변경된 GET 방식 컨트롤러
    @GetMapping
    public GitHubPrResponseWrapper getPullRequests(@RequestParam Long userId) {
        return prService.getPullRequests(userId);
    }
}
