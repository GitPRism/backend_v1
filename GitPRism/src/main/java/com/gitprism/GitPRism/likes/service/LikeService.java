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
   * ✅ 좋아요 생성 및 복구
   */
  @Transactional
  public Map<String, Object> addLike(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    Like like = likeRepository.findByUserAndPortfolio(user, portfolio).orElse(null);

    if (like != null) {
      if (Boolean.FALSE.equals(like.getIsDeleted())) {
        throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
      }
      like.recover(); // isDeleted = false, updatedAt 수정
    } else {
      like = Like.create(user, portfolio);
      likeRepository.save(like);
    }

    // 🔔 알림 전송
    eventPublisher.publishEvent(new LikeCreatedEvent(like));

    return Map.of(
        "message", "좋아요가 추가되었습니다.",
        "code", 201,
        "portfolio_id", portfolioId,
        "user_id", user.getId()
    );
  }


  @Transactional
  public Map<String, Object> cancelLike(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    Like like = likeRepository.findByUserAndPortfolioAndIsDeletedFalse(user, portfolio)
        .orElseThrow(() -> new NoSuchElementException("좋아요 기록이 존재하지 않습니다."));

    like.delete();

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "좋아요가 취소되었습니다.");
    response.put("code", 200);
    response.put("portfolio_id", portfolioId);
    response.put("user_id", user.getId());

    return response;
  }
}
