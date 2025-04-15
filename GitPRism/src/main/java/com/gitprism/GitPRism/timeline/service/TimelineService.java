package com.gitprism.GitPRism.timeline.service;

import com.gitprism.GitPRism.comments.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TimelineService {

  public void createTimeline(Comment comment) {
    log.info("🕒 타임라인 기록 - commentId: {}, content: {}",
        comment.getId(), comment.getComment());

    // TODO: 타임라인 저장 또는 WebSocket 전송
  }
}
