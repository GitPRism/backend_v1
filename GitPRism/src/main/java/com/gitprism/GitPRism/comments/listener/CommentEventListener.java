package com.gitprism.GitPRism.comments.listener;

import com.gitprism.GitPRism.comments.event.CommentCreatedEvent;
import com.gitprism.GitPRism.notification.service.NotificationService;
import com.gitprism.GitPRism.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener {

  private final NotificationService notificationService;
  private final TimelineService timelineService;

  @Async
  @EventListener
  public void handleCommentCreated(CommentCreatedEvent event) {
    log.info("💬 댓글 이벤트 수신: {}", event.getComment().getId());

    // ✅ 알림 저장 및 WebSocket 전송
    notificationService.createNotification(event.getComment());

    // ✅ 타임라인 저장 및 WebSocket 전송
    timelineService.createTimeline(event.getComment());
  }
}
