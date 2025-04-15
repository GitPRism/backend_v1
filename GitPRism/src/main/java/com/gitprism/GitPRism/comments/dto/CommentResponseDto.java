package com.gitprism.GitPRism.comments.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import com.gitprism.GitPRism.comments.entity.Comment;

@Builder
@Getter
public class CommentResponseDto {
  private Long portfolioId;
  private String userName;
  private String comment;
  private LocalDateTime createdAt;

  public static CommentResponseDto of(Comment comment, String userName) {
    return CommentResponseDto.builder()
        .portfolioId(comment.getPortfolio().getId())
        .userName(userName)
        .comment(comment.getComment())
        .createdAt(comment.getCreatedAt())
        .build();
  }
}
