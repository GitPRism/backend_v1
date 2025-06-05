package com.gitprism.GitPRism.notification.entity;
import com.gitprism.GitPRism.notification.entity.Notification;
import com.gitprism.GitPRism.notification.entity.NotificationType;
import com.gitprism.GitPRism.notification.repository.NotificationRepository;

public enum NotificationType {
  COMMENT_CREATED,
  LIKE_ADDED,
  BOOKMARK_ADDED,
  INVITE_SENT
}
