package com.gitprism.GitPRism.portfolios.controller.websocket;

import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import com.gitprism.GitPRism.portfolio_collaborators.service.PortfolioCollaboratorService;
import com.gitprism.GitPRism.portfolios.dto.websocket.*;
import com.gitprism.GitPRism.portfolios.service.EditHistoryRedisService;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditTimestampStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.gitprism.GitPRism.portfolios.dto.redis.EditHistory;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EditWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final PortfolioEditTimestampStore timestampStore;
  private final PortfolioEditDraftStore draftStore;
  private final EditHistoryRedisService editHistoryRedisService;
  private final PortfolioCollaboratorService collaboratorService;

  private final Map<Long, Set<String>> activeUsersMap = new ConcurrentHashMap<>();

  @MessageMapping("/edit")
  public void handleEdit(EditMessage message) {
    Long portfolioId = message.getPortfolioId();
    Long editorId = message.getEditorId();
    Long incomingTimestamp = message.getTimestamp();

    // ✅ EDITOR 권한만 편집 가능
    if (!collaboratorService.hasEditorPermission(portfolioId, editorId)) {
      log.warn("❌ 편집 차단 (EDITOR 아님): editorId={}, portfolioId={}", editorId, portfolioId);
      return;
    }

    Long latestTimestamp = timestampStore.getLatestTimestamp(portfolioId);
    if (incomingTimestamp < latestTimestamp) return;

    draftStore.saveDraft(portfolioId, message.getContent());
    timestampStore.updateTimestamp(portfolioId, incomingTimestamp);
    editHistoryRedisService.saveHistory(
        EditHistory.builder()
            .portfolioId(portfolioId)
            .editorId(editorId)
            .editorName(null)
            .content(message.getContent())
            .timestamp(incomingTimestamp)
            .build()
    );

    EditResponse response = new EditResponse(
        portfolioId,
        editorId,
        message.getContent(),
        String.valueOf(incomingTimestamp)
    );
    messagingTemplate.convertAndSend("/topic/portfolio." + portfolioId, response);
  }

  @MessageMapping("/typing")
  public void handleTyping(TypingIndicatorMessage msg) {
    // ✅ VIEWER는 타이핑 전송 불가
    if (!collaboratorService.hasEditorPermission(msg.getPortfolioId(), msg.getEditorId())) {
      log.warn("❌ 타이핑 차단 (EDITOR 아님): editorId={}, portfolioId={}", msg.getEditorId(), msg.getPortfolioId());
      return;
    }

    messagingTemplate.convertAndSend("/topic/typing." + msg.getPortfolioId(), msg);
  }

  @MessageMapping("/join")
  public void handleJoin(ActiveUserJoinRequest joinRequest) {
    Long portfolioId = joinRequest.getPortfolioId();
    Long editorId = joinRequest.getEditorId();
    String editorName = joinRequest.getEditorName();

    // ❗ 권한이 존재하지 않으면 접속 거부 (VIEWER 이상이어야만 허용)
    if (!collaboratorService.isAlreadyCollaborator(portfolioId, editorId)) {
      log.warn("❌ 협업자 아님 (접속 차단): editorId={}, portfolioId={}", editorId, portfolioId);
      return;
    }

    activeUsersMap.computeIfAbsent(portfolioId, k -> ConcurrentHashMap.newKeySet()).add(editorName);
    broadcastActiveUsers(portfolioId);
  }

  private void broadcastActiveUsers(Long portfolioId) {
    List<String> users = new ArrayList<>(activeUsersMap.getOrDefault(portfolioId, Set.of()));
    messagingTemplate.convertAndSend(
        "/topic/active." + portfolioId,
        new ActiveUserMessage(portfolioId, users)
    );
    log.info("🔄 접속 사용자 목록 전송: {}", users);
  }
}
