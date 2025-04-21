package com.gitprism.GitPRism.portfolios.listener;

import com.gitprism.GitPRism.portfolios.score.PortfolioScoreEvent;
import com.gitprism.GitPRism.portfolios.score.PortfolioScoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.mockito.Mockito.*;

class PortfolioScoreEventListenerTest {

  private RedisTemplate<String, String> redisTemplate;
  private ZSetOperations<String, String> zSetOperations;
  private PortfolioScoreEventListener listener;

  @BeforeEach
  void setUp() {
    redisTemplate = mock(RedisTemplate.class);
    zSetOperations = mock(ZSetOperations.class);
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

    listener = new PortfolioScoreEventListener(redisTemplate);
  }

  @Test
  void testHandleScoreEvent_forLike() {
    // given
    Long portfolioId = 1L;
    PortfolioScoreEvent event = new PortfolioScoreEvent(portfolioId, PortfolioScoreType.LIKE);

    // when
    listener.handleScoreEvent(event);

    // then
    verify(zSetOperations, times(1)).incrementScore(
        "popular_portfolios", "portfolio:1", 2.0
    );
  }

  @Test
  void testHandleScoreEvent_forBookmark() {
    Long portfolioId = 5L;
    PortfolioScoreEvent event = new PortfolioScoreEvent(portfolioId, PortfolioScoreType.BOOKMARK);

    listener.handleScoreEvent(event);

    verify(zSetOperations, times(1)).incrementScore(
        "popular_portfolios", "portfolio:5", 3.0
    );
  }
}
