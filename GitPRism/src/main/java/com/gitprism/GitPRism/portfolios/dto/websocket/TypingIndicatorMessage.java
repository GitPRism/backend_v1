package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingIndicatorMessage {
  private Long portfolioId;
  private Long editorId;
  private String editorName;
  private boolean typing;
  private Long timestamp;
}
