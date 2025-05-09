package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDetailResponse {
    private Long portfolioId;
    private String username;
    private String title;
    private String description;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int bookmarkCount;
    private int contentCount;
}