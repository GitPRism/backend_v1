package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicPortfolioResponse {
    private Long portfolioId;
    private String title;
    private String username;
    private String status;
    private LocalDateTime created_at;
    private int likeCount;
    private int bookmarkCount;
    private int commentCount;
}

