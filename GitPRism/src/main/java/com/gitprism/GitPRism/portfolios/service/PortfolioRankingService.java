package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import com.gitprism.GitPRism.portfolios.dto.response.PopularPortfolioResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PopularPortfolioResponse.PopularPortfolioDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioRankingService {

  private final RedisTemplate<String, String> redisTemplate;
  private final PortfolioRepository portfolioRepository;

  public PopularPortfolioResponse getPopularPortfolios(int limit) {
    String redisKey = "popular_portfolios";
    Set<ZSetOperations.TypedTuple<String>> ranking = redisTemplate.opsForZSet()
        .reverseRangeWithScores(redisKey, 0, limit - 1);

    if (ranking == null || ranking.isEmpty()) {
      return new PopularPortfolioResponse("인기 포트폴리오 없음", 200, 0, List.of());
    }

    List<Long> portfolioIds = ranking.stream()
        .map(tuple -> Long.parseLong(tuple.getValue()))
        .collect(Collectors.toList());

    Map<Long, Double> scoreMap = ranking.stream()
        .collect(Collectors.toMap(
            tuple -> Long.parseLong(tuple.getValue()),
            ZSetOperations.TypedTuple::getScore
        ));

    List<Portfolio> portfolios = portfolioRepository.findByIdIn(portfolioIds);

    Map<Long, Portfolio> portfolioMap = portfolios.stream()
        .collect(Collectors.toMap(Portfolio::getId, p -> p));

    List<PopularPortfolioDto> result = portfolioIds.stream()
        .map(id -> {
          Portfolio p = portfolioMap.get(id);
          return new PopularPortfolioDto(p.getId(), p.getTitle(), p.getUser().getUsername(), scoreMap.get(id));
        })
        .collect(Collectors.toList());

    return new PopularPortfolioResponse("인기 포트폴리오 조회 성공", 200, result.size(), result);
  }
}
