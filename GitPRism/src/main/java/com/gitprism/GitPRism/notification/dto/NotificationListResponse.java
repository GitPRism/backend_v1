package com.gitprism.GitPRism.notification.dto;

import com.gitprism.GitPRism.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationListResponse {
  private String message;
  private int code;
  private List<NotificationDto> notifications;

  @Getter
  @AllArgsConstructor
  public static class NotificationDto {
    private Long notificationId;
    private NotificationType type;
    private String message;
    private Long portfolioId;
    private String redirectUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
  }
}
