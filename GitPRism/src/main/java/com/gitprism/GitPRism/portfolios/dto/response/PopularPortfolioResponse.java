package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PopularPortfolioResponse {
  private String message;
  private int code;
  private int count;
  private List<PopularPortfolioDto> data;

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PopularPortfolioDto {
    private Long portfolioId;
    private String title;
    private String author;
    private String description;
    private Double score;
  }
}
