package com.gitprism.GitPRism.portfolios.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioEditTimestampStore {

  private final StringRedisTemplate redisTemplate;

  public Long getLatestTimestamp(Long portfolioId) {
    String key = getKey(portfolioId);
    String value = redisTemplate.opsForValue().get(key);
    return value != null ? Long.parseLong(value) : 0L;
  }

  public void updateTimestamp(Long portfolioId, Long timestamp) {
    String key = getKey(portfolioId);
    redisTemplate.opsForValue().set(key, timestamp.toString());
  }

  private String getKey(Long portfolioId) {
    return "portfolio:" + portfolioId + ":lastEditTimestamp";
  }
}
