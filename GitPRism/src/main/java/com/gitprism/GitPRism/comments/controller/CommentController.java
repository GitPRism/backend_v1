package com.gitprism.GitPRism.comments.controller;

import com.gitprism.GitPRism.comments.dto.CommentRequestDto;
import com.gitprism.GitPRism.comments.dto.CommentResponseDto;
import com.gitprism.GitPRism.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  // ✅ POST - 댓글 생성: /api/v1/portfolios/{portfolioId}/comments
  @PostMapping("/api/v1/portfolios/{portfolioId}/comments")
  public ResponseEntity<CommentResponseDto> createComment(
      @PathVariable Long portfolioId,
      @RequestBody @Valid CommentRequestDto requestDto,
      Authentication authentication
  ) {
    String githubId = authentication.getName();  // JWT에서 추출된 사용자 ID
    CommentResponseDto response = commentService.createComment(portfolioId, githubId, requestDto);
    return ResponseEntity.status(201).body(response);
  }

  // ✅ GET - 댓글 조회: /api/v1/comments/{portfolioId}
  @GetMapping("/api/v1/comments/{portfolioId}")
  public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long portfolioId) {
    return ResponseEntity.ok(commentService.getCommentsByPortfolioId(portfolioId));
  }

  @PutMapping("/api/v1/comments/{commentId}")
  public ResponseEntity<Map<String, Object>> updateComment(
      @PathVariable Long commentId,
      @RequestBody @Valid CommentRequestDto requestDto,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> updated = commentService.updateComment(commentId, githubId, requestDto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/api/v1/comments/{commentId}")
  public ResponseEntity<Map<String, Object>> deleteComment(
      @PathVariable Long commentId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> result = commentService.deleteComment(commentId, githubId);
    return ResponseEntity.ok(result);
  }
}
