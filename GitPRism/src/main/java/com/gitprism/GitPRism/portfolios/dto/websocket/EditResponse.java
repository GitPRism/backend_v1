package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditResponse {
  private Long portfolioId;
  private Long editorId;
  private String content;
  private String field;     // 추가됨: "title" 또는 "description"
  private String timestamp; // ISO 포맷 또는 Long timestamp도 가능
}
