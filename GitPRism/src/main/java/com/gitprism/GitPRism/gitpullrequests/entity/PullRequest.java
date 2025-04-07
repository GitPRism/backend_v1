package com.gitprism.GitPRism.gitpullrequests.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pull_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long githubPrId;

    private Integer number;
    private String prTitle;

    @Column(columnDefinition = "TEXT")
    private String prDetails;

    private Boolean draft;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime mergedAt;

    private String author;
    private String sha;
    private String sourceBranch;
    private String targetBranch;

    @Column(columnDefinition = "TEXT")
    private String htmlUrl;

    @Column(columnDefinition = "TEXT")
    private String labels;

    @Builder.Default
    private Boolean isDeleted = false;
}

