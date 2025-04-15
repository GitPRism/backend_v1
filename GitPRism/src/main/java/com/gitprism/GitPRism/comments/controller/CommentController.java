package com.gitprism.GitPRism.comments.controller;

import com.gitprism.GitPRism.comments.dto.CommentRequestDto;
import com.gitprism.GitPRism.comments.dto.CommentResponseDto;
import com.gitprism.GitPRism.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/portfolios/{portfolioId}/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentResponseDto> createComment(
      @PathVariable Long portfolioId,
      @RequestBody @Valid CommentRequestDto requestDto,
      Authentication authentication
  ) {
    String githubId = authentication.getName();  // JWT에서 추출된 사용자 ID
    CommentResponseDto response = commentService.createComment(portfolioId, githubId, requestDto);
    return ResponseEntity.status(201).body(response);
  }
}
