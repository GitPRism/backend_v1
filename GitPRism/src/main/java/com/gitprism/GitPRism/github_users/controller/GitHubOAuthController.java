package com.gitprism.GitPRism.github_users.controller;

import com.gitprism.GitPRism.github_users.dto.response.LoginResponseDto;
import com.gitprism.GitPRism.github_users.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
@Tag(name = "GitHub OAuth", description = "GitHub 로그인 API")
public class GitHubOAuthController {

  private final OAuthService oAuthService;

  @Operation(summary = "GitHub OAuth 로그인 URL 생성",
      description = "프론트엔드는 이 URL로 유저를 리디렉션시켜 GitHub 로그인을 시작합니다.")
  @GetMapping("/oauth-url")
  public ResponseEntity<String> getLoginUrl() {
    String loginUrl = oAuthService.generateGitHubLoginUrl();
    return ResponseEntity.ok(loginUrl);
  }

  @Operation(summary = "GitHub OAuth 로그인 처리",
      description = "GitHub 로그인 후 redirect_uri로 받은 code를 전달하면 JWT를 포함한 로그인 정보를 반환합니다.")
  @GetMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @Parameter(description = "GitHub에서 리턴받은 인증 코드", required = true)
      @RequestParam String code) {
    return ResponseEntity.ok(oAuthService.loginWithGitHub(code));
  }

  @Operation(summary = "GitHub OAuth 로그인 성공 여부 및 소프트 삭제 사용자 조회", description = "JWT 인증된 사용자의 GitHub ID를 반환합니다.")
  @GetMapping("/logincheck")
  public ResponseEntity<?> getMyInfo(Authentication authentication) {
    String githubId = authentication.getName(); // JWT에서 sub 값
    return ResponseEntity.ok("내 GitHub ID: " + githubId);
  }
}
