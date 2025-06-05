package com.gitprism.GitPRism.github_users.repository;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitHubUserRepository extends JpaRepository<GitHubUser, Long> {
  Optional<GitHubUser> findByGithubId(String githubId);
}
