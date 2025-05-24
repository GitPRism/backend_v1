package com.gitprism.GitPRism.portfolios.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.gitprism.GitPRism.portfolios.document.PortfolioDocument;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioIndexer {

  private final PortfolioRepository portfolioRepository;
  private final ElasticsearchClient elasticsearchClient;

  @PostConstruct
  public void indexPortfolios() {
    portfolioRepository.findAll().forEach(portfolio -> {
      try {
        PortfolioDocument doc = PortfolioDocument.builder()
            .id(portfolio.getId())
            .title(portfolio.getTitle())
            .description(portfolio.getDescription())
            .createdAt(portfolio.getCreatedAt())
            .updatedAt(portfolio.getUpdatedAt())
            .build();

        elasticsearchClient.index(i -> i
            .index("portfolios")
            .id(doc.getId().toString())
            .document(doc)
        );
        log.info("✅ Indexed portfolio: {}", doc.getTitle());
      } catch (Exception e) {
        log.error("❌ Failed to index portfolio ID {}", portfolio.getId(), e);
      }
    });
  }
}