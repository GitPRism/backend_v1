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
    private String username;
    private String avatarUrl;
    private String repoOrgAvatarUrl;
    private boolean bookmarked;
    private boolean liked;
    private int likeCount;
    private int bookmarkCount;
    private int commentCount;
    private LocalDateTime createdAt;
  }
}
