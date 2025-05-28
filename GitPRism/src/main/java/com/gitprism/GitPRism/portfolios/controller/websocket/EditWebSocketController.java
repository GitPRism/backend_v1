package com.gitprism.GitPRism.portfolios.controller.websocket;

import com.gitprism.GitPRism.portfolio_collaborators.service.PortfolioCollaboratorService;
import com.gitprism.GitPRism.portfolios.dto.redis.EditHistory;
import com.gitprism.GitPRism.portfolios.dto.websocket.*;
import com.gitprism.GitPRism.portfolios.service.EditHistoryRedisService;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditTimestampStore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket을 통한 실시간 포트폴리오 협업 편집을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Hidden // Swagger UI에서는 숨기되 문서에는 남기고 싶은 경우
public class EditWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;
  private final PortfolioEditTimestampStore timestampStore;
  private final PortfolioEditDraftStore draftStore;
  private final EditHistoryRedisService editHistoryRedisService;
  private final PortfolioCollaboratorService collaboratorService;

  private final Map<Long, Set<String>> activeUsersMap = new ConcurrentHashMap<>();

  /**
   * 클라이언트로부터 /app/edit 메시지를 수신하고, 편집 내용을 Redis에 저장 및 브로드캐스트합니다.
   */
  @MessageMapping("/edit")
  public void handleEdit(EditMessage message) {
    Long portfolioId = message.getPortfolioId();
    Long editorId = message.getEditorId();
    Long incomingTimestamp = message.getTimestamp();
    String field = message.getField(); // "title" 또는 "description"

    if (!collaboratorService.hasEditorPermission(portfolioId, editorId)) {
      log.warn("❌ 편집 차단 (EDITOR 아님): editorId={}, portfolioId={}", editorId, portfolioId);
      return;
    }

    Long latestTimestamp = timestampStore.getLatestTimestamp(portfolioId);
    if (incomingTimestamp < latestTimestamp) return;

    if ("title".equals(field)) {
      draftStore.saveTitle(portfolioId, message.getContent());
    } else if ("description".equals(field)) {
      draftStore.saveDescription(portfolioId, message.getContent());
    } else {
      log.warn("❌ 알 수 없는 필드: {}", field);
      return;
    }

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
        field,
        String.valueOf(incomingTimestamp)
    );

    messagingTemplate.convertAndSend("/topic/portfolio." + portfolioId, response);
  }

  /**
   * 클라이언트가 입력 중일 때 /app/typing 메시지를 전송하며, 서버는 이를 구독자에게 전달합니다.
   */
  @MessageMapping("/typing")
  public void handleTyping(TypingIndicatorMessage msg) {
    if (!collaboratorService.hasEditorPermission(msg.getPortfolioId(), msg.getEditorId())) {
      log.warn("❌ 타이핑 차단 (EDITOR 아님): editorId={}, portfolioId={}", msg.getEditorId(), msg.getPortfolioId());
      return;
    }

    messagingTemplate.convertAndSend("/topic/typing." + msg.getPortfolioId(), msg);
  }

  /**
   * 사용자가 편집방에 입장하면 /app/join 메시지를 통해 서버에 알리고,
   * 서버는 접속자 목록을 모두에게 전송합니다.
   */
  @MessageMapping("/join")
  public void handleJoin(ActiveUserJoinRequest joinRequest) {
    Long portfolioId = joinRequest.getPortfolioId();
    Long editorId = joinRequest.getEditorId();
    String editorName = joinRequest.getEditorName();

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
