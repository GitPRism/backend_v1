package com.gitprism.GitPRism.portfolios.controller.websocket;

import com.gitprism.GitPRism.portfolios.dto.websocket.EditMessage;
import com.gitprism.GitPRism.portfolios.dto.websocket.EditResponse;
import com.gitprism.GitPRism.portfolios.dto.websocket.TypingIndicatorMessage;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditTimestampStore;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EditWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final PortfolioEditTimestampStore timestampStore;
  private final PortfolioEditDraftStore draftStore;

  /**
   * 실시간 편집 메시지 처리
   */
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
    timestampStore.updateTimestamp(portfolioId, incomingTimestamp);

    EditResponse response = new EditResponse(
        message.getPortfolioId(),
        message.getEditorId(),
        message.getContent(),
        incomingTimestamp.toString()
    );

    messagingTemplate.convertAndSend("/topic/portfolio." + portfolioId, response);
  }

  /**
   * 입력 중 표시 메시지 처리
   */
  @MessageMapping("/typing")
  public void handleTyping(TypingIndicatorMessage message) {
    Long portfolioId = message.getPortfolioId();
    messagingTemplate.convertAndSend("/topic/typing." + portfolioId, message);
  }
}
