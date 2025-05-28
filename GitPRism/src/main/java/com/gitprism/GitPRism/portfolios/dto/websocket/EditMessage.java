package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditMessage {
  private Long portfolioId;
  private Long editorId;
  private String content;
  private String field; // "title" 또는 "description"
  private Long timestamp;
}

