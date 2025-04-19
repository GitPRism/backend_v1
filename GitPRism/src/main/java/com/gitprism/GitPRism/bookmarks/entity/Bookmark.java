package com.gitprism.GitPRism.bookmarks.entity;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private GitHubUser user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id", nullable = false)
  private Portfolio portfolio;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  public static Bookmark create(GitHubUser user, Portfolio portfolio) {
    return Bookmark.builder()
        .user(user)
        .portfolio(portfolio)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .isDeleted(false)
        .build();
  }

  public void delete() {
    this.isDeleted = true;
    this.updatedAt = LocalDateTime.now();
  }

  public void recover() {
    this.isDeleted = false;
    this.updatedAt = LocalDateTime.now();
  }
}
