package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioRankingInitializer {

  private final RedisTemplate<String, String> redisTemplate;
  private final PortfolioRepository portfolioRepository;

  private static final String POPULAR_KEY = "popular_portfolios";

  @EventListener(ContextRefreshedEvent.class)
  public void restoreRanking() {
    log.info("🔄 Redis 인기 포트폴리오 복원 시작...");

    List<Portfolio> published = portfolioRepository.findAllByStatusAndIsDeletedFalse(Portfolio.Status.PUBLISHED);

    for (Portfolio portfolio : published) {
      String member = String.valueOf(portfolio.getId());

      // 이미 점수가 설정된 포트폴리오는 건너뛰기
      Double currentScore = redisTemplate.opsForZSet().score(POPULAR_KEY, member);
      if (currentScore == null) {
        // 점수 계산 로직 추가 (예시)
        Double score = calculateScore(portfolio);
        redisTemplate.opsForZSet().add(POPULAR_KEY, member, score);
        log.info("✅ 포트폴리오 {} 복원 (점수: {})", portfolio.getId(), score);
      } else {
        log.info("🔄 포트폴리오 {} 기존 점수 유지 (점수: {})", portfolio.getId(), currentScore);
      }
    }

    log.info("✅ Redis 복원 완료! 총 {}개 포트폴리오", published.size());
  }

  private Double calculateScore(Portfolio portfolio) {
    // TODO: 실제 점수 계산 로직
    // 기본 점수는 10.0으로 가정
    return 10.0;
  }
}
