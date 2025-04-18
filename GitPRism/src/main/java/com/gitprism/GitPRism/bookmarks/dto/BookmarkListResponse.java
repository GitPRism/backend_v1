package com.gitprism.GitPRism.bookmarks.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BookmarkListResponse {
  private String message;
  private int code;
  private List<BookmarkSimpleDto> bookmarks;

  @Getter
  @AllArgsConstructor
  public static class BookmarkSimpleDto {
    private Long portfolioId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
  }
}
