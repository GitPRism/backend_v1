package com.gitprism.GitPRism.portfolios.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InviteMessage {
  private Long portfolioId;
  private Long inviteeId;
}
