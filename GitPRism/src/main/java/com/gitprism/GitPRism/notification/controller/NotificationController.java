package com.gitprism.GitPRism.notification.controller;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.notification.dto.NotificationListResponse;
import com.gitprism.GitPRism.notification.service.NotificationService;
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
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final GitHubUserRepository userRepository;

  //  GET - 내 알림 목록 조회: /api/v1/notifications
  @Operation(summary = "내 알림 목록 조회", description = "인증된 사용자의 모든 알림 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 목록을 성공적으로 조회했습니다.",
          content = @Content(schema = @Schema(implementation = NotificationListResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "404", description = "GitHub 사용자를 찾을 수 없습니다.")
  })
  @GetMapping
  public ResponseEntity<NotificationListResponse> getMyNotifications(Authentication authentication) {
    String githubId = authentication.getName();
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    return ResponseEntity.ok(notificationService.getMyNotifications(user));
  }

  //  PATCH - 개별 알림 읽음 처리: /api/v1/notifications/{notificationId}/read
  @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림이 성공적으로 읽음 처리되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"알림이 읽음 처리되었습니다.\", \"notificationId\": 1 }"))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "404", description = "알림 또는 GitHub 사용자를 찾을 수 없습니다.")
  })
  @PatchMapping("/{notificationId}/read")
  public ResponseEntity<Map<String, Object>> markAsRead(
      @PathVariable Long notificationId,
      Authentication authentication
  ) {
    String githubId = authentication.getName();
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Map<String, Object> result = notificationService.markAsRead(notificationId, user);
    return ResponseEntity.ok(result);
  }

  //  PATCH - 모든 알림 읽음 처리: /api/v1/notifications/read-all
  @Operation(summary = "모든 알림 읽음 처리", description = "인증된 사용자의 모든 알림을 읽음 상태로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "모든 알림이 성공적으로 읽음 처리되었습니다.",
          content = @Content(schema = @Schema(example = "{ \"message\": \"모든 알림이 읽음 처리되었습니다.\" }"))),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
      @ApiResponse(responseCode = "404", description = "GitHub 사용자를 찾을 수 없습니다.")
  })
  @PatchMapping("/read-all")
  public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
    String githubId = authentication.getName();
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Map<String, Object> result = notificationService.markAllAsRead(user);
    return ResponseEntity.ok(result);
  }
}
