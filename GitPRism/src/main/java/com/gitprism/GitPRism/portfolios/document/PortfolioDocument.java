package com.gitprism.GitPRism.portfolios.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDocument implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long id;
  private String title;
  private String description;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
