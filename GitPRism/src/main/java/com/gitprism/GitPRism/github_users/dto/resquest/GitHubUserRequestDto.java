package com.gitprism.GitPRism.github_users.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserRequestDto {
  private String githubId;
  private String username;
  private String email;
  private String accessToken;
}
