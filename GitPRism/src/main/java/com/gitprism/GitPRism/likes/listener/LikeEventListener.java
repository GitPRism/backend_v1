package com.gitprism.GitPRism.likes.listener;

import com.gitprism.GitPRism.likes.entity.Like;
import com.gitprism.GitPRism.likes.event.LikeCreatedEvent;
import com.gitprism.GitPRism.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeEventListener {

  private final NotificationService notificationService;

  @EventListener
  public void handleLikeCreatedEvent(LikeCreatedEvent event) {
    Like like = event.getLike();
    notificationService.createNotification(like);
  }
}
