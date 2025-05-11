package com.gitprism.GitPRism.gitrepositorys.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "repos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "git_id")
    private String gitId;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "repo_name", length = 255)
    private String repoName;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_branch", length = 255)
    private String defaultBranch;

    @Column(length = 255)
    private String language;

    @Column(length = 50)
    private String visibility;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "github_repo_id", unique = true)
    private Long githubRepoId;

    @Column(name = "org_avatar_url", columnDefinition = "TEXT")
    private String orgAvatarUrl;

}
