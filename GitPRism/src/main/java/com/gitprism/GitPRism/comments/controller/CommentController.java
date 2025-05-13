package com.gitprism.GitPRism.comments.controller;

import com.gitprism.GitPRism.comments.dto.CommentRequestDto;
import com.gitprism.GitPRism.comments.dto.CommentResponseDto;
import com.gitprism.GitPRism.comments.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  //  POST - 댓글 생성: /api/v1/portfolios/{portfolioId}/comments
  @Operation(summary = "댓글 생성", description = "특정 포트폴리오에 새로운 댓글을 작성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "댓글이 성공적으로 생성되었습니다.",
          content = @Content(schema = @Schema(implementation = CommentResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
  })
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

  //  GET - 댓글 조회: /api/v1/comments/{portfolioId}
  @Operation(summary = "댓글 조회", description = "특정 포트폴리오의 모든 댓글을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 목록을 성공적으로 조회했습니다.",
          content = @Content(schema = @Schema(example = "{ \"comments\": [{ \"id\": 1, \"author\": \"user123\", \"content\": \"좋은 포트폴리오네요!\", \"createdAt\": \"2025-05-13T10:00:00\" }] }"))),
      @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
  })
  @GetMapping("/api/v1/comments/{portfolioId}")
  public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long portfolioId) {
    return ResponseEntity.ok(commentService.getCommentsByPortfolioId(portfolioId));
  }

  //  PUT - 댓글 수정: /api/v1/comments/{commentId}
  @Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 수정되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"댓글이 수정되었습니다.\" }"))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "403", description = "해당 댓글에 대한 수정 권한이 없습니다."),
      @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
  })
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

  //  DELETE - 댓글 삭제: /api/v1/comments/{commentId}
  @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 삭제되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"댓글이 삭제되었습니다.\" }"))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "403", description = "해당 댓글에 대한 삭제 권한이 없습니다."),
      @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
  })
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
