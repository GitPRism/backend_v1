package com.gitprism.GitPRism.portfolios.entity;

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

  // ManyToOne으로 작성자 참조
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
}
