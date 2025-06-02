package com.gitprism.GitPRism.portfolio_collaborators.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioCollaboratorResponse {
  private Long portfolioId;
  private Long userId;
  private String role;
  private String message;
}