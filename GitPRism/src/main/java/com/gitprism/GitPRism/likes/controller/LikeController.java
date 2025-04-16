package com.gitprism.GitPRism.likes.controller;

import com.gitprism.GitPRism.likes.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/api/v1/portfolios/{portfolioId}/likes")
  public ResponseEntity<Map<String, Object>> likePortfolio(
      @PathVariable Long portfolioId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    Map<String, Object> response = likeService.addLike(portfolioId, githubId);
    return ResponseEntity.status(201).body(response);
  }
}
