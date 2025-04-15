package com.gitprism.GitPRism.notification.service;

import com.gitprism.GitPRism.comments.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

  public void createNotification(Comment comment) {
    log.info("알림 생성 - portfolioId: {}, userId: {}",
        comment.getPortfolio().getId(), comment.getUser().getId());

    // TODO: 알림 저장 또는 WebSocket 전송
  }
}
