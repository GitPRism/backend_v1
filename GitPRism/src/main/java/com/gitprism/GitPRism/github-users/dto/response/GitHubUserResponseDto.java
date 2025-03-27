package com.gitprism.GitPRism.github_users.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubUserResponseDto {
  private Long id;
  private String githubId;
  private String username;
  private String email;
  private String accessToken;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean deleted; // ✅ deleted로 정확히 명시!
}
