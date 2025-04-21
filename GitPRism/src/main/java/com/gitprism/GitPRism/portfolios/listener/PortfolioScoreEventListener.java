package com.gitprism.GitPRism.portfolios.listener;

import com.gitprism.GitPRism.portfolios.score.PortfolioScoreEvent;
import com.gitprism.GitPRism.portfolios.score.PortfolioScoreType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioScoreEventListener {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String POPULAR_KEY = "popular_portfolios";

  @Async
  @EventListener
  public void handleScoreEvent(PortfolioScoreEvent event) {
    double score = getScoreByType(event.getType());
    String member = String.valueOf(event.getPortfolioId());

    redisTemplate.opsForZSet().incrementScore(POPULAR_KEY, member, score);
    log.info("🔥 Redis 점수 업데이트 → {} (+{})", member, score);
  }

  private double getScoreByType(PortfolioScoreType type) {
    return switch (type) {
      case COMMENT -> 1.0;
      case LIKE -> 2.0;
      case BOOKMARK -> 3.0;
      case VIEW -> 0.5;
    };
  }
}
