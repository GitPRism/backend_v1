/*package com.gitprism.GitPRism.github_users.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  @Operation(summary = "GitHub OAuth 로그인 성공 여부", description = "JWT 인증된 사용자의 GitHub ID를 반환합니다.")
  @GetMapping("/logincheck")
  public ResponseEntity<?> getMyInfo(Authentication authentication) {
    String githubId = authentication.getName(); // JWT에서 sub 값
    return ResponseEntity.ok("내 GitHub ID: " + githubId);
  }
}
*/