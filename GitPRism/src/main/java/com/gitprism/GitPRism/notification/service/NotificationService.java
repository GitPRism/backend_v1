package com.gitprism.GitPRism.notification.service;

import com.gitprism.GitPRism.comments.entity.Comment;
import com.gitprism.GitPRism.likes.entity.Like;
import com.gitprism.GitPRism.bookmarks.entity.Bookmark;
import com.gitprism.GitPRism.notification.entity.Notification;
import com.gitprism.GitPRism.notification.entity.NotificationType;
import com.gitprism.GitPRism.notification.repository.NotificationRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
