package com.gitprism.GitPRism.github_users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GitHubUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String githubId;
  private String username;
  private String email;

  @Column(length = 1000)
  private String accessToken;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Builder.Default
  private Boolean deleted = false;  // ← 필드명 변경!
}
