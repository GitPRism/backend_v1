package com.gitprism.GitPRism.likes.event;

import com.gitprism.GitPRism.likes.entity.Like;
import lombok.Getter;

@Getter
public class LikeCreatedEvent {
  private final Like like;

  public LikeCreatedEvent(Like like) {
    this.like = like;
  }
}
