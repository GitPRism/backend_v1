package com.gitprism.GitPRism.bookmarks.listener;

import com.gitprism.GitPRism.bookmarks.event.BookmarkCreatedEvent;
import com.gitprism.GitPRism.notification.entity.Notification;
import com.gitprism.GitPRism.notification.entity.NotificationType;
import com.gitprism.GitPRism.notification.repository.NotificationRepository;
import com.gitprism.GitPRism.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookmarkEventListener {

  private final NotificationService notificationService;

  @EventListener
  public void handleBookmarkCreated(BookmarkCreatedEvent event) {
    notificationService.createNotification(event.getBookmark());
  }
}
