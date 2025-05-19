package com.gitprism.GitPRism.portfolios.entity;

import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import jakarta.persistence.*;
import lombok.*;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Portfolio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private GitHubUser user;

  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private Boolean isDeleted;

  public enum Status {
    DRAFT, PUBLISHED
  }
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "repo_id")
  private Repo repo;

  @Column(name = "repo_org_avatar_url", columnDefinition = "TEXT")
  private String repoOrgAvatarUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Portfolio parent;
}