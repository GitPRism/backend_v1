package com.gitprism.GitPRism.portfolio_collaborators.dto.request;

import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCollaboratorRequest {
  private Long userId;
  private PortfolioCollaborator.Role role;
}
