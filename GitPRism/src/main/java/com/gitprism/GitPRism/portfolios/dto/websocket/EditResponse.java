package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditResponse {
  private Long portfolioId;
  private Long editorId;
  private String content;
  private String timestamp; // ISO 포맷 예: 2025-05-27T00:00:00Z
}
