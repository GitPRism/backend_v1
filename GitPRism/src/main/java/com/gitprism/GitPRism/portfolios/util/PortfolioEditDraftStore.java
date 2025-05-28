package com.gitprism.GitPRism.portfolios.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioEditDraftStore {

  private final StringRedisTemplate redisTemplate;

  private String titleKey(Long portfolioId) {
    return "portfolio:" + portfolioId + ":draft:title";
  }

  private String descKey(Long portfolioId) {
    return "portfolio:" + portfolioId + ":draft:description";
  }

  public void saveTitle(Long portfolioId, String title) {
    redisTemplate.opsForValue().set(titleKey(portfolioId), title);
  }

  public void saveDescription(Long portfolioId, String description) {
    redisTemplate.opsForValue().set(descKey(portfolioId), description);
  }

  public String getTitle(Long portfolioId) {
    return redisTemplate.opsForValue().get(titleKey(portfolioId));
  }

  public String getDescription(Long portfolioId) {
    return redisTemplate.opsForValue().get(descKey(portfolioId));
  }
}
