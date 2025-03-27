package com.gitprism.GitPRism.github_users.controller;

import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.service.GitHubUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github-users")
@RequiredArgsConstructor
@Tag(name = "GitHub 사용자 API", description = "GitHub 사용자 조회/삭제 기능 제공")
public class GitHubUserController {

  private final GitHubUserService service;

  @GetMapping("/{userId}")
  @Operation(summary = "Git 사용자 조회", description = "userId로 GitHub 사용자 정보를 조회합니다.")
  public ResponseEntity<GitHubUserResponseDto> getUser(@PathVariable Long userId) {
    // 👇 현재 사용자 정보 확인 (디버깅용)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("🔐 현재 사용자: " + auth);
    System.out.println("🧩 Principal: " + auth.getPrincipal());
    System.out.println("🛡️ Roles: " + auth.getAuthorities());
    return ResponseEntity.ok(service.findById(userId));
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "Git 사용자 삭제", description = "userId로 사용자를 삭제합니다.")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    service.delete(userId);
    return ResponseEntity.ok().body(
        Map.of("message", "사용자가 삭제되었습니다.", "userId", userId)
    );
  }

  @PatchMapping("/{userId}/restore")
  @Operation(summary = "GitHub 사용자 복구", description = "소프트 삭제된 GitHub 사용자를 복구합니다.")
  public ResponseEntity<?> restoreUser(@PathVariable Long userId) {
    service.restore(userId);
    return ResponseEntity.ok().body(
        Map.of("message", "사용자가 복구되었습니다.", "userId", userId)
    );
  }
}
