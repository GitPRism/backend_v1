package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioDraftController {

  private final PortfolioEditDraftStore draftStore;

  @GetMapping("/{portfolioId}/draft")
  public String getDraft(@PathVariable Long portfolioId) {
    return draftStore.getDraft(portfolioId);
  }
}
