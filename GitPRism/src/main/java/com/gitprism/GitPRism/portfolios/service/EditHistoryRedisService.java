package com.gitprism.GitPRism.portfolios.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitprism.GitPRism.portfolios.dto.redis.EditHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditHistoryRedisService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private String getKey(Long portfolioId) {
    return "editHistory:" + portfolioId;
  }

  public void saveHistory(EditHistory history) {
    try {
      String key = getKey(history.getPortfolioId());
      redisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(history));

      // 🔥 TTL 설정 (예: 7일 후 자동 만료)
      redisTemplate.expire(key, Duration.ofDays(7));

    } catch (Exception e) {
      log.error("❌ Redis 저장 실패: {}", e.getMessage());
    }
  }


  public List<Object> getHistory(Long portfolioId) {
    String key = getKey(portfolioId);
    return redisTemplate.opsForList().range(key, 0, -1);
  }
}
