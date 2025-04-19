package com.gitprism.GitPRism.notification.controller;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.notification.dto.NotificationListResponse;
import com.gitprism.GitPRism.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final GitHubUserRepository userRepository;

  @GetMapping
  public ResponseEntity<NotificationListResponse> getMyNotifications(Authentication authentication) {
    String githubId = authentication.getName();
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    return ResponseEntity.ok(notificationService.getMyNotifications(user));
  }

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

}
