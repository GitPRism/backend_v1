package com.gitprism.GitPRism.bookmarks.controller;

import com.gitprism.GitPRism.bookmarks.service.BookmarkService;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkListResponse;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkStatusResponse;
import com.gitprism.GitPRism.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class BookmarkController {

  private final BookmarkService bookmarkService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/{portfolioId}/bookmarks")
  public ResponseEntity<Map<String, Object>> addBookmark(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> result = bookmarkService.addBookmark(portfolioId, githubId);
    return ResponseEntity.status(201).body(result);
  }

  @GetMapping("/bookmarks/me")
  public ResponseEntity<BookmarkListResponse> getMyBookmarks(Authentication authentication) {
    String githubId = authentication.getName();
    BookmarkListResponse response = bookmarkService.getMyBookmarks(githubId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/portfolios/{portfolioId}/bookmark")
  public ResponseEntity<BookmarkStatusResponse> getBookmarkStatus(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    BookmarkStatusResponse response = bookmarkService.getBookmarkStatus(portfolioId, githubId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{portfolioId}/bookmarks")
  public ResponseEntity<Map<String, Object>> cancelBookmark(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> result = bookmarkService.cancelBookmark(portfolioId, githubId);
    return ResponseEntity.ok(result);
  }
}
