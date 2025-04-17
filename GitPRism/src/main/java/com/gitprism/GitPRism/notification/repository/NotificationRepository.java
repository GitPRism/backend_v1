package com.gitprism.GitPRism.notification.repository;

import com.gitprism.GitPRism.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
