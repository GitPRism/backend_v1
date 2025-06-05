package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.dto.request.PortfolioBatchRequest;
import com.gitprism.GitPRism.portfolios.dto.request.PortfolioUpdateRequest;
import com.gitprism.GitPRism.portfolios.dto.response.*;
import com.gitprism.GitPRism.portfolios.service.PortfolioService;
import com.gitprism.GitPRism.portfolios.service.PrSummaryService;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "포트폴리오 생성 API", description = "pr 코드 + 내용 분석, pr 내용으로 포트폴리오 생성")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PrSummaryService prSummaryService;
    private final com.gitprism.GitPRism.github_users.service.GitHubUserService gitHubUserService;

    @Operation(summary = "포트폴리오 생성", description = "레포지토리의 PR 분석을 바탕으로 포트폴리오를 생성합니다. 병합된 PR만 분석 대상이 됩니다.")
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @RequestParam("repoId") Long repoId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        PortfolioResponse response = portfolioService.createPortfolio(user.getId(), repoId, null);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @Operation(summary = "다중 레포 기반 포트폴리오 생성")
    @PostMapping("/batch")
    public ResponseEntity<PortfolioBatchResponse> createBatchPortfolios(
            @RequestBody PortfolioBatchRequest request,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);

        PortfolioBatchResponse response = portfolioService.createBatch(user.getId(), request.getRepoIds());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 포트폴리오 전체 조회")
    public ResponseEntity<PortfolioBatchResponse> getMyPortfolios(
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(portfolioService.getMyPortfolios(userId, pageable));
    }


    @Operation(summary = "포트폴리오 상세 조회", description = "포트폴리오 ID를 통해 제목, 설명, 수정일자, 좋아요/댓글/북마크 수를 조회합니다.")
    @GetMapping("/{portfolioId}")
    public ResponseEntity<CombinedPortfolioDetailResponse> getPortfolioDetail(
            @PathVariable Long portfolioId,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        Long userId = user.getId();

        return ResponseEntity.ok(portfolioService.getPortfolioDetail(portfolioId, userId));
    }


    @Operation(summary = "전체 공개 포트폴리오 목록 조회", description = "PUBLISHED 상태의 포트폴리오 전체를 반환합니다.")
    @GetMapping("/public")
    public ResponseEntity<PortfolioBatchResponse> getPublicPortfolios(
            @RequestParam(defaultValue = "0") int page
    ) {
        int size = 6;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 원하는 정렬 기준
        Pageable pageable = PageRequest.of(page, size, sort);

        PortfolioBatchResponse response = portfolioService.getPublicPortfolios(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포트폴리오 게시 상태 토글", description = "포트폴리오 상태를 PUBLISHED ↔ DRAFT 로 변경합니다.")
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


    @PatchMapping("/{portfolioId}")
    @Operation(summary = "개별 포트폴리오 수정 (연결된 다중 포트폴리오 자동 갱신)")
    public ResponseEntity<PortfolioResponse> updateIndividualPortfolio(
            @PathVariable Long portfolioId,
            @RequestBody PortfolioUpdateRequest request,
            Authentication authentication
    ) {
        String githubId = authentication.getName();
        GitHubUserResponseDto user = gitHubUserService.findByGithubId(githubId);
        PortfolioResponse response = portfolioService.updateIndividualPortfolio(user.getId(), portfolioId, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
