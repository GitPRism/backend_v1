package com.gitprism.GitPRism.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCountResponse {
  private String message;
  private int code;
  private Long portfolioId;
  private int likeCount;
}
