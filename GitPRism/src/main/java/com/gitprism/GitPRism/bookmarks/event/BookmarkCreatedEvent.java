package com.gitprism.GitPRism.bookmarks.event;

import com.gitprism.GitPRism.bookmarks.entity.Bookmark;
import lombok.Getter;

@Getter
public class BookmarkCreatedEvent {
  private final Bookmark bookmark;

  public BookmarkCreatedEvent(Bookmark bookmark) {
    this.bookmark = bookmark;
  }
}
