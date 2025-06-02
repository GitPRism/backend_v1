package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUserMessage {
  private Long portfolioId;
  private List<String> activeEditors;
}
