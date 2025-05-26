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
  private Long timestamp; // 💡 EpochMillis (System.currentTimeMillis())
}
