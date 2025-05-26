package com.gitprism.GitPRism.portfolio_collaborators.service;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import com.gitprism.GitPRism.portfolio_collaborators.repository.PortfolioCollaboratorRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioCollaboratorService {

  private final PortfolioCollaboratorRepository collaboratorRepository;
  private final PortfolioRepository portfolioRepository;
  private final GitHubUserRepository userRepository;

  public boolean isAlreadyCollaborator(Long portfolioId, Long userId) {
    return collaboratorRepository.existsByPortfolioIdAndUserId(portfolioId, userId);
  }

  public PortfolioCollaborator addCollaborator(Long portfolioId, Long userId, PortfolioCollaborator.Role role) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다."));
    GitHubUser user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    PortfolioCollaborator collaborator = new PortfolioCollaborator();
    collaborator.setPortfolio(portfolio);
    collaborator.setUser(user);
    collaborator.setRole(role);

    return collaboratorRepository.save(collaborator);
  }
}
