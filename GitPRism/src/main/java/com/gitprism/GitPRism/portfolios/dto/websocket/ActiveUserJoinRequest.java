package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUserJoinRequest {
  private Long portfolioId;
  private String editorName;
}
