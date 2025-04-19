package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.dto.response.PrSummaryResponse;
import com.gitprism.GitPRism.portfolios.service.PrSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;


@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "포트폴리오 생성 API", description = "pr 코드 + 내용 분석, pr 내용으로 포트폴리오 생성")
public class PrSummaryController {

    private final PrSummaryService prSummaryService;
    private final com.gitprism.GitPRism.github_users.service.GitHubUserService gitHubUserService;


    @Operation(summary = "PR 분석 요약 리스트 생성 + 조회", description = "레포 ID를 기반으로 사용자가 만든 PR을 분석해 포트폴리오 요약 데이터를 반환합니다.")
    @PostMapping("/summaries")
    public ResponseEntity<PrSummaryResponse> getPrSummary(
            @RequestParam Long repoId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        Long userId = user.getId();

        PrSummaryResponse response = prSummaryService.summarizeRepository(userId, repoId);
        return ResponseEntity.ok(response);
    }
}
