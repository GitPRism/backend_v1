package com.gitprism.GitPRism.bookmarks.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkStatusResponse {
  private String message;
  private int code;
  private Long portfolioId;
  private boolean bookmarked;
}
