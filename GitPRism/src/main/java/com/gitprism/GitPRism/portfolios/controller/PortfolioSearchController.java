package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.service.PortfolioSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 검색 API")
public class PortfolioSearchController {

  private final PortfolioSearchService portfolioSearchService;

  @GetMapping("/search")
  @Operation(summary = "포트폴리오 검색", description = "포트폴리오 제목 및 설명에서 검색")
  public ResponseEntity<List<Portfolio>> searchPortfolios(@RequestParam String query) throws IOException {
    List<Portfolio> portfolios = portfolioSearchService.searchPortfolios(query);
    return ResponseEntity.ok(portfolios);
  }
}
