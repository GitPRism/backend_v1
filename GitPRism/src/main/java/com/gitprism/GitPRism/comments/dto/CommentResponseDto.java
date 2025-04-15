package com.gitprism.GitPRism.comments.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import com.gitprism.GitPRism.comments.entity.Comment;

@Getter
@Builder
public class CommentResponseDto {

  private Long portfolioId;
  private String userName;
  private String comment;
  private int contentCount;
  private LocalDateTime createdAt;

  public static CommentResponseDto of(Comment comment, String userName, int contentCount) {
    return CommentResponseDto.builder()
        .portfolioId(comment.getPortfolio().getId())
        .userName(userName)
        .comment(comment.getComment())  // 🔧 Entity 필드명과 맞추세요
        .contentCount(contentCount)
        .createdAt(comment.getCreatedAt())
        .build();
  }
}
