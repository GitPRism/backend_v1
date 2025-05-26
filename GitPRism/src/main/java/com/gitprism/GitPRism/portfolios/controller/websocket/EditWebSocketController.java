package com.gitprism.GitPRism.portfolios.controller.websocket;

import com.gitprism.GitPRism.portfolios.dto.websocket.EditMessage;
import com.gitprism.GitPRism.portfolios.dto.websocket.EditResponse;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditTimestampStore;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;

@Controller
@RequiredArgsConstructor
public class EditWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final PortfolioEditTimestampStore timestampStore;
  private final PortfolioEditDraftStore draftStore; // ✅ 추가

  @MessageMapping("/edit")
  public void handleEdit(EditMessage message) {
    Long portfolioId = message.getPortfolioId();
    Long incomingTimestamp = message.getTimestamp();

    Long latestTimestamp = timestampStore.getLatestTimestamp(portfolioId);

    // 🔐 충돌 방지: 이전 편집보다 오래된 경우 무시
    if (incomingTimestamp < latestTimestamp) {
      return;
    }

    draftStore.saveDraft(portfolioId, message.getContent());
    // 최신 메시지 반영
    timestampStore.updateTimestamp(portfolioId, incomingTimestamp);

    EditResponse response = new EditResponse(
        message.getPortfolioId(),
        message.getEditorId(),
        message.getContent(),
        incomingTimestamp.toString()
    );

    messagingTemplate.convertAndSend("/topic/portfolio." + portfolioId, response);
  }
}
