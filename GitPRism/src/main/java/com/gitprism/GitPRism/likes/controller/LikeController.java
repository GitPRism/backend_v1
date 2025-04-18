package com.gitprism.GitPRism.likes.controller;

import com.gitprism.GitPRism.likes.service.LikeService;
import com.gitprism.GitPRism.config.jwt.JwtTokenProvider;
import com.gitprism.GitPRism.likes.dto.LikeCountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios") // ✅ 공통 경로 설정
public class LikeController {

  private final LikeService likeService;
  private final JwtTokenProvider jwtTokenProvider;

  // ✅ 좋아요 등록
  @PostMapping("/{portfolioId}/likes")
  @Operation(summary = "좋아요 등록", description = "포트폴리오에 좋아요를 등록합니다.")
  public ResponseEntity<Map<String, Object>> likePortfolio(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> response = likeService.addLike(portfolioId, githubId);
    return ResponseEntity.status(201).body(response);
  }

  // ✅ 좋아요 취소
  @DeleteMapping("/{portfolioId}/likes")
  @Operation(summary = "좋아요 취소", description = "특정 포트폴리오에 대해 사용자의 좋아요를 취소합니다.")
  public ResponseEntity<Map<String, Object>> cancelLike(
      @Parameter(hidden = true) @RequestHeader("Authorization") String token,
      @PathVariable Long portfolioId
  ) {
    String githubId = jwtTokenProvider.getGithubIdFromToken(token.replace("Bearer ", "").trim());
    Map<String, Object> result = likeService.cancelLike(portfolioId, githubId);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{portfolioId}/likes")
  @Operation(summary = "좋아요 수 조회", description = "특정 포트폴리오의 좋아요 수를 조회합니다.")
  public ResponseEntity<LikeCountResponse> getLikeCount(
      @PathVariable Long portfolioId
  ) {
    LikeCountResponse response = likeService.getLikeCount(portfolioId);
    return ResponseEntity.ok(response);
  }
}
