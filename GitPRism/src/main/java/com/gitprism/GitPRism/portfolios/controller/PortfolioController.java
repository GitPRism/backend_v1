package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.dto.response.PortfolioResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PrSummaryResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PortfolioDetailResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PublicPortfolioResponse;
import com.gitprism.GitPRism.portfolios.service.PortfolioService;
import com.gitprism.GitPRism.portfolios.service.PrSummaryService;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "포트폴리오 생성 API", description = "pr 코드 + 내용 분석, pr 내용으로 포트폴리오 생성")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PrSummaryService prSummaryService;
    private final com.gitprism.GitPRism.github_users.service.GitHubUserService gitHubUserService;

    @Operation(
            summary = "포트폴리오 생성",
            description = "레포지토리의 PR 분석을 바탕으로 포트폴리오를 생성합니다. 병합된 PR만 분석 대상이 됩니다."
    )
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @RequestParam("repoId") Long repoId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        PortfolioResponse response = portfolioService.createPortfolio(user.getId(), repoId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

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

    @GetMapping("/me")
    @Operation(summary = "내 포트폴리오 목록 조회", description = "로그인한 사용자의 포트폴리오를 조회합니다.")
    public ResponseEntity<List<PortfolioResponse>> getMyPortfolios(Authentication authentication) {
        String githubId = authentication.getName();

        // mock 로그인일 경우: user_id = 1 고정
        if (githubId.equals("mock") || githubId.equals("12345678")) {
            return ResponseEntity.ok(portfolioService.getPortfoliosByUser(1L));
        }

        // 일반 사용자 흐름
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        List<PortfolioResponse> portfolios = portfolioService.getPortfoliosByUser(user.getId());
        return ResponseEntity.ok(portfolios);
    }

    @Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 ID를 통해 제목, 설명, 수정일자를 조회합니다.")
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDetailResponse> getPortfolioDetail(
            @PathVariable Long portfolioId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        gitHubUserService.findByGithubId(githubId); // 유저 인증 확인용 (사용 안 해도 문제 없음)
        PortfolioDetailResponse response = portfolioService.getPortfolioDetail(portfolioId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포트폴리오 상태 토글", description = "포트폴리오 상태를 draft와 publish로 전환합니다.")
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> togglePortfolioStatus(
            @PathVariable Long portfolioId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        PortfolioResponse response = portfolioService.togglePortfolioStatus(user.getId(), portfolioId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "전체 공개 포트폴리오 목록 조회", description = "PUBLISHED 상태의 포트폴리오 전체를 반환합니다.")
    @GetMapping("/public")
    public ResponseEntity<Map<String, List<PublicPortfolioResponse>>> getAllPublicPortfolios() {
        List<PublicPortfolioResponse> data = portfolioService.getAllPublicPortfolios();
        return ResponseEntity.ok(Map.of("data", data));
    }
}