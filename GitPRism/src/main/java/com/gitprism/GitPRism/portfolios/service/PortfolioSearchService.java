package com.gitprism.GitPRism.portfolios.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioSearchService {

  private final ElasticsearchClient elasticsearchClient;

  public List<Portfolio> searchPortfolios(String query) throws IOException {
    SearchRequest searchRequest = new SearchRequest.Builder()
        .index("portfolios")
        .query(q -> q
            .multiMatch(m -> m
                .query(query)
                .fields("title", "description")
                .fuzziness("AUTO") // 🔍 유사어 검색 허용 (예: 포트폴리오 ↔ 포트, 폴리오 등)
            )
        )
        .size(100)
        .build();

    SearchResponse<Portfolio> response = elasticsearchClient.search(searchRequest, Portfolio.class);
    return response.hits().hits().stream()
        .map(Hit::source)
        .collect(Collectors.toList());
  }
}
