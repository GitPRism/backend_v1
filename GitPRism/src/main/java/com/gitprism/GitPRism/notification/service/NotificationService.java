package com.gitprism.GitPRism.notification.service;

import com.gitprism.GitPRism.comments.entity.Comment;
import com.gitprism.GitPRism.likes.entity.Like;
import com.gitprism.GitPRism.bookmarks.entity.Bookmark;
import com.gitprism.GitPRism.notification.entity.Notification;
import com.gitprism.GitPRism.notification.entity.NotificationType;
import com.gitprism.GitPRism.notification.repository.NotificationRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.notification.dto.NotificationListResponse;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public void createNotification(Comment comment) {
    Notification notification = Notification.create(
        comment.getPortfolio().getUser(),
        comment.getUser(),
        comment.getPortfolio(),
        NotificationType.COMMENT_CREATED,
        comment.getUser().getUsername() + "님이 댓글을 남겼습니다.",
        "/portfolios/" + comment.getPortfolio().getId()
    );
    notificationRepository.save(notification);
  }

  public void createNotification(Like like) {
    Notification notification = Notification.create(
        like.getPortfolio().getUser(),
        like.getUser(),
        like.getPortfolio(),
        NotificationType.LIKE_ADDED,
        like.getUser().getUsername() + "님이 좋아요를 눌렀습니다.",
        "/portfolios/" + like.getPortfolio().getId()
    );
    notificationRepository.save(notification);
  }

  public void createNotification(Bookmark bookmark) {
    Notification notification = Notification.create(
        bookmark.getPortfolio().getUser(), // 알림 받을 사람 (포트폴리오 주인)
        bookmark.getUser(),                // 북마크한 사람
        bookmark.getPortfolio(),
        NotificationType.BOOKMARK_ADDED,
        bookmark.getUser().getUsername() + "님이 포트폴리오를 북마크했습니다.",
        "/portfolios/" + bookmark.getPortfolio().getId()
    );
    notificationRepository.save(notification);
  }

  @Transactional(readOnly = true)
  public NotificationListResponse getMyNotifications(GitHubUser user) {
    List<Notification> notifications = notificationRepository
        .findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId());

    List<NotificationListResponse.NotificationDto> results = notifications.stream()
        .map(n -> new NotificationListResponse.NotificationDto(
            n.getId(),
            n.getType(),
            n.getMessage(),
            n.getPortfolio().getId(),
            n.getRedirectUrl(),
            n.getIsRead(),
            n.getCreatedAt()
        ))
        .toList();

    return new NotificationListResponse("알림 목록 조회 성공", 200, results);
  }
}
