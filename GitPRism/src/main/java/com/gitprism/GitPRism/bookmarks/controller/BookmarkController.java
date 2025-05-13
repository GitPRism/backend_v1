package com.gitprism.GitPRism.bookmarks.controller;

import com.gitprism.GitPRism.bookmarks.service.BookmarkService;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkListResponse;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkStatusResponse;
import com.gitprism.GitPRism.config.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "북마크 추가", description = "특정 포트폴리오에 북마크를 추가합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "북마크가 성공적으로 추가되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"북마크가 추가되었습니다.\", \"portfolioId\": 1, \"bookmarkCount\": 5 }"))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 포트폴리오 ID입니다."),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
  })
  @PostMapping("/{portfolioId}/bookmarks")
  public ResponseEntity<Map<String, Object>> addBookmark(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> result = bookmarkService.addBookmark(portfolioId, githubId);
    return ResponseEntity.status(201).body(result);
  }

  @Operation(summary = "내 북마크 조회", description = "인증된 사용자의 모든 북마크를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "북마크 목록을 성공적으로 조회했습니다.",
          content = @Content(schema = @Schema(implementation = BookmarkListResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
  })
  @GetMapping("/bookmarks/me")
  public ResponseEntity<BookmarkListResponse> getMyBookmarks(Authentication authentication) {
    String githubId = authentication.getName();
    BookmarkListResponse response = bookmarkService.getMyBookmarks(githubId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "북마크 상태 조회", description = "특정 포트폴리오에 대해 사용자가 북마크를 추가했는지 상태를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "북마크 상태를 성공적으로 조회했습니다.",
          content = @Content(schema = @Schema(implementation = BookmarkStatusResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 포트폴리오 ID입니다."),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
  })
  @GetMapping("/{portfolioId}/bookmark")
  public ResponseEntity<BookmarkStatusResponse> getBookmarkStatus(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    BookmarkStatusResponse response = bookmarkService.getBookmarkStatus(portfolioId, githubId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "북마크 취소", description = "특정 포트폴리오에서 북마크를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "북마크가 성공적으로 취소되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"북마크가 취소되었습니다.\", \"portfolioId\": 1, \"bookmarkCount\": 4 }"))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 포트폴리오 ID입니다."),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
  })
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
