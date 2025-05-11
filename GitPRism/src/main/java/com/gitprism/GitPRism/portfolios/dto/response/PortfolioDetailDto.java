package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDetailDto {
    private Long portfolioId;
    private String username;
    private String avatarUrl;
    private String repoName;
    private String repoUrl;
    private String title;
    private String description;
    private int likeCount;
    private boolean liked;
    private int bookmarkCount;
    private boolean bookmarked;
    private String status;
    private LocalDateTime createdAt;
}