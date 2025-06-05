package com.gitprism.GitPRism.portfolio_collaborators.service;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import com.gitprism.GitPRism.portfolio_collaborators.repository.PortfolioCollaboratorRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import com.gitprism.GitPRism.notification.entity.Notification;
import com.gitprism.GitPRism.notification.entity.NotificationType;
import com.gitprism.GitPRism.notification.repository.NotificationRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioCollaboratorService {

  private final PortfolioCollaboratorRepository collaboratorRepository;
  private final PortfolioRepository portfolioRepository;
  private final GitHubUserRepository userRepository;
  private final NotificationRepository notificationRepository;

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

  public boolean hasEditorPermission(Long portfolioId, Long userId) {
    return collaboratorRepository
        .findByPortfolioIdAndUserId(portfolioId, userId)
        .map(collaborator -> collaborator.getRole() == PortfolioCollaborator.Role.EDITOR)
        .orElse(false);
  }

  public Optional<PortfolioCollaborator.Role> getCollaboratorRole(Long portfolioId, Long userId) {
    return collaboratorRepository
        .findByPortfolioIdAndUserId(portfolioId, userId)
        .map(PortfolioCollaborator::getRole);
  }

  public void updateCollaboratorRole(Long portfolioId, Long userId, PortfolioCollaborator.Role newRole) {
    PortfolioCollaborator collaborator = collaboratorRepository
        .findByPortfolioIdAndUserId(portfolioId, userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 협업자가 존재하지 않습니다."));

    collaborator.setRole(newRole);
    collaboratorRepository.save(collaborator);
  }

  public void removeCollaborator(Long portfolioId, Long userId) {
    PortfolioCollaborator collaborator = collaboratorRepository
        .findByPortfolioIdAndUserId(portfolioId, userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 협업자가 존재하지 않습니다."));

    collaboratorRepository.delete(collaborator);
  }

  @Transactional
  public void sendInviteNotification(Long inviteeId, Long inviterId, Long portfolioId, String inviterName) {
    GitHubUser invitee = userRepository.findById(inviteeId)
        .orElseThrow(() -> new IllegalArgumentException("초대받는 유저가 존재하지 않습니다."));
    GitHubUser inviter = userRepository.findById(inviterId)
        .orElseThrow(() -> new IllegalArgumentException("초대한 유저가 존재하지 않습니다."));
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다."));

    // ✅ 포트폴리오 ID 포함 메시지 생성
    String message = inviter.getUsername() + "님이 " + portfolio.getId() + "번 포트폴리오에 초대했습니다.";

    Notification notification = Notification.create(
        invitee,
        inviter,
        portfolio,
        NotificationType.INVITE_SENT,
        message,
        "/portfolios/" + portfolioId
    );

    notificationRepository.save(notification);
  }
}
