package com.gitprism.GitPRism.comments.dto;

import com.gitprism.GitPRism.comments.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentListResponseDto {

  private Long commentId;
  private String userName;
  private String comment;
  private LocalDateTime createdAt;

  public static CommentListResponseDto of(Comment comment, String userName) {
    return CommentListResponseDto.builder()
        .commentId(comment.getId())
        .userName(userName)
        .comment(comment.getComment())
        .createdAt(comment.getCreatedAt())
        .build();
  }
}
