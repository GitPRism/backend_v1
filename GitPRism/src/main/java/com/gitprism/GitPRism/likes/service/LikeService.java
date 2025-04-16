package com.gitprism.GitPRism.likes.service;

import com.gitprism.GitPRism.likes.entity.Like;
import com.gitprism.GitPRism.likes.event.LikeCreatedEvent;
import com.gitprism.GitPRism.likes.repository.LikeRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final GitHubUserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * ✅ 좋아요 생성
   */
  @Transactional
  public Map<String, Object> addLike(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    boolean exists = likeRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, portfolio);
    if (exists) {
      throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
    }

    Like like = Like.builder()
        .user(user)
        .portfolio(portfolio)
        .isDeleted(false)
        .build();
    likeRepository.save(like);

    // 🔔 이벤트 발행 (알림 전송용)
    eventPublisher.publishEvent(new LikeCreatedEvent(like));

    // ✅ 응답 반환
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "좋아요가 추가되었습니다.");
    response.put("code", 201);
    response.put("portfolio_id", portfolioId);
    response.put("user_id", user.getId());

    return response;
  }
}
