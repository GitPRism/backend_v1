package com.gitprism.GitPRism.notification.repository;

import com.gitprism.GitPRism.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
  List<Notification> findAllByUserAndIsReadFalseAndIsDeletedFalse(GitHubUser user);
}
