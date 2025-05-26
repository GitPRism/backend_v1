package com.gitprism.GitPRism.portfolios.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioEditDraftStore {

  private final StringRedisTemplate redisTemplate;

  public void saveDraft(Long portfolioId, String content) {
    redisTemplate.opsForValue().set(getKey(portfolioId), content);
  }

  public String getDraft(Long portfolioId) {
    return redisTemplate.opsForValue().get(getKey(portfolioId));
  }

  private String getKey(Long portfolioId) {
    return "portfolio:" + portfolioId + ":draft";
  }
}
