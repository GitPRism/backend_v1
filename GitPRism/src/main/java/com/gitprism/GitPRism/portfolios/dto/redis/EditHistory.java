package com.gitprism.GitPRism.portfolios.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditHistory {
  private Long portfolioId;
  private Long editorId;
  private String editorName;
  private String content;
  private Long timestamp;
}
