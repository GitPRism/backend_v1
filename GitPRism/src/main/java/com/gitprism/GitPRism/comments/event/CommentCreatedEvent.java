package com.gitprism.GitPRism.comments.event;

import com.gitprism.GitPRism.comments.entity.Comment;
import lombok.Getter;

@Getter
public class CommentCreatedEvent {

  private final Comment comment;

  public CommentCreatedEvent(Comment comment) {
    this.comment = comment;
  }
}
