package com.gitprism.GitPRism.github_users.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
  private String token;
  private String githubId;
  private String username;
  private String email;
}
