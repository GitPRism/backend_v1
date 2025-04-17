package com.gitprism.GitPRism.likes.repository;

import com.gitprism.GitPRism.likes.entity.Like;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

  // 소프트 삭제 고려해서 중복 여부 확인
  boolean existsByUserAndPortfolioAndIsDeletedFalse(GitHubUser user, Portfolio portfolio);

  // 알림 발행 등을 위한 엔티티 조회
  Optional<Like> findByUserAndPortfolioAndIsDeletedFalse(GitHubUser user, Portfolio portfolio);

  Optional<Like> findByUserAndPortfolio(GitHubUser user, Portfolio portfolio);

}