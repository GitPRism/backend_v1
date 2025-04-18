package com.gitprism.GitPRism.bookmarks.repository;

import com.gitprism.GitPRism.bookmarks.entity.Bookmark;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  boolean existsByUserAndPortfolioAndIsDeletedFalse(GitHubUser user, Portfolio portfolio);
  Optional<Bookmark> findByUserAndPortfolio(GitHubUser user, Portfolio portfolio);
  Optional<Bookmark> findByUserAndPortfolioAndIsDeletedFalse(GitHubUser user, Portfolio portfolio);
}
