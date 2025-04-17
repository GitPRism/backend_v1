package com.gitprism.GitPRism.notification.entity;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 알림 받는 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private GitHubUser user;

  // 알림 발생시킨 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private GitHubUser sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  private Portfolio portfolio;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(columnDefinition = "TEXT")
  private String message;

  private Boolean isRead;
  private String redirectUrl;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  private Boolean isDeleted;

  public static Notification create(GitHubUser receiver, GitHubUser sender,
                                    Portfolio portfolio, NotificationType type,
                                    String message, String redirectUrl) {
    return Notification.builder()
        .user(receiver)
        .sender(sender)
        .portfolio(portfolio)
        .type(type)
        .message(message)
        .redirectUrl(redirectUrl)
        .isRead(false)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public void markAsRead() {
    this.isRead = true;
    this.updatedAt = LocalDateTime.now();
  }

  public void delete() {
    this.isDeleted = true;
    this.updatedAt = LocalDateTime.now();
  }
}
