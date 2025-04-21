package com.gitprism.GitPRism.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTestService {

  private final StringRedisTemplate redisTemplate;
  private final Environment env;

  @EventListener(ApplicationReadyEvent.class)
  public void testRedis() {
    try {
      String redisHost = env.getProperty("spring.redis.host");
      String redisPort = env.getProperty("spring.redis.port");
      System.out.println("📍 현재 Redis 호스트: " + redisHost);
      System.out.println("📍 현재 Redis 포트: " + redisPort);

      redisTemplate.opsForValue().set("test", "ok");
      String value = redisTemplate.opsForValue().get("test");
      System.out.println("✅ Redis 연결 성공: " + value);
    } catch (Exception e) {
      System.out.println("❌ Redis 연결 실패: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
