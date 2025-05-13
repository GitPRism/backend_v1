package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.service.PortfolioRankingService;
import com.gitprism.GitPRism.portfolios.dto.response.PopularPortfolioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class PortfolioRankingController {

  private final PortfolioRankingService portfolioRankingService;

  //  GET - 인기 포트폴리오 조회: /api/v1/portfolios/popular
  @Operation(summary = "인기 포트폴리오 조회", description = "인기 있는 포트폴리오를 조회합니다. 기본적으로 상위 10개의 포트폴리오를 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "인기 포트폴리오 목록을 성공적으로 조회했습니다.",
          content = @Content(schema = @Schema(implementation = PopularPortfolioResponse.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
  })
  @GetMapping("/popular")
  public ResponseEntity<PopularPortfolioResponse> getPopularPortfolios(
      @RequestParam(defaultValue = "10") int limit
  ) {
    PopularPortfolioResponse response = portfolioRankingService.getPopularPortfolios(limit);
    return ResponseEntity.ok(response);
  }
}
