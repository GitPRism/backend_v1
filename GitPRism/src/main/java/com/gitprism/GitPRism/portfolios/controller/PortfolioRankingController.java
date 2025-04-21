package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.service.PortfolioRankingService;
import com.gitprism.GitPRism.portfolios.dto.response.PopularPortfolioResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PopularPortfolioResponse.PopularPortfolioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class PortfolioRankingController {

  private final PortfolioRankingService portfolioRankingService;

  @GetMapping("/popular")
  public ResponseEntity<PopularPortfolioResponse> getPopularPortfolios(
      @RequestParam(defaultValue = "10") int limit
  ) {
    PopularPortfolioResponse response = portfolioRankingService.getPopularPortfolios(limit);
    return ResponseEntity.ok(response);
  }
}
