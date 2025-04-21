package com.gitprism.GitPRism.portfolios.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioScoreEvent {
  private final Long portfolioId;
  private final PortfolioScoreType type;
}
