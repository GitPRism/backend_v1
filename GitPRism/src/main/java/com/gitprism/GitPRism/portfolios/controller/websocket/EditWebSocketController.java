package com.gitprism.GitPRism.portfolios.controller.websocket;

import com.gitprism.GitPRism.portfolios.dto.websocket.*;
import com.gitprism.GitPRism.portfolios.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class EditWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final PortfolioEditTimestampStore timestampStore;
  private final PortfolioEditDraftStore draftStore;

  private final Map<Long, Set<String>> activeUsersMap = new ConcurrentHashMap<>();

  @MessageMapping("/edit")
  public void handleEdit(EditMessage message) {
    Long portfolioId = message.getPortfolioId();
    Long incomingTimestamp = message.getTimestamp();

    Long latestTimestamp = timestampStore.getLatestTimestamp(portfolioId);
    if (incomingTimestamp < latestTimestamp) return;

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

  @MessageMapping("/typing")
  public void handleTyping(TypingIndicatorMessage msg) {
    messagingTemplate.convertAndSend("/topic/typing." + msg.getPortfolioId(), msg);
  }

  @MessageMapping("/join")
  public void handleJoin(ActiveUserJoinRequest joinRequest) {
    Long portfolioId = joinRequest.getPortfolioId();
    String editorName = joinRequest.getEditorName();

    activeUsersMap.computeIfAbsent(portfolioId, k -> ConcurrentHashMap.newKeySet()).add(editorName);
    broadcastActiveUsers(portfolioId);
  }

  private void broadcastActiveUsers(Long portfolioId) {
    List<String> users = new ArrayList<>(activeUsersMap.getOrDefault(portfolioId, Set.of()));
    messagingTemplate.convertAndSend(
        "/topic/active." + portfolioId,
        new ActiveUserMessage(portfolioId, users)
    );
  }
}
