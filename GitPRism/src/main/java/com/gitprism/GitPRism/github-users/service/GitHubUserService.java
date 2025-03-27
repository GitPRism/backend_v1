package com.gitprism.GitPRism.github_users.service;

import com.gitprism.GitPRism.github_users.dto.request.GitHubUserRequestDto;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubUserService {

  private final GitHubUserRepository repository;

  public GitHubUserResponseDto findById(Long id) {
    GitHubUser user = repository.findById(id)
        .filter(u -> !Boolean.TRUE.equals(u.getDeleted())) // ✅ 삭제된 사용자는 걸러줌!
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없거나 삭제된 사용자입니다. ID: " + id));
    return toDto(user);
  }

  public void delete(Long id) {
    GitHubUser user = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("삭제할 사용자가 없습니다. ID: " + id));
    user.setDeleted(true); // ✅ 소프트 삭제
    repository.save(user);
  }

  public void restore(Long id) {
    GitHubUser user = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("복구할 사용자가 없습니다. ID: " + id));

    if (!user.getDeleted()) {
      throw new IllegalStateException("이미 활성화된 사용자입니다. ID: " + id);
    }

    user.setDeleted(false);
    repository.save(user);
  }
  private GitHubUserResponseDto toDto(GitHubUser user) {
    return GitHubUserResponseDto.builder()
        .id(user.getId())
        .githubId(user.getGithubId())
        .username(user.getUsername())
        .email(user.getEmail())
        .accessToken(user.getAccessToken())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .deleted(user.getDeleted())
        .build();
  }
}
