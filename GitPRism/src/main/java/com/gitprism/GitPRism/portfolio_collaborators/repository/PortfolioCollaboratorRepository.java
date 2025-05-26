package com.gitprism.GitPRism.portfolio_collaborators.repository;

import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioCollaboratorRepository extends JpaRepository<PortfolioCollaborator, Long> {
  boolean existsByPortfolioIdAndUserId(Long portfolioId, Long userId);
  List<PortfolioCollaborator> findByPortfolioId(Long portfolioId);
}
