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
    private String repoName;
    private String repoUrl;
    private String username;
    private String avatarUrl;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private boolean bookmarked;
}