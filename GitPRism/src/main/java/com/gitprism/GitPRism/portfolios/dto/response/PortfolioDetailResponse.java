package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDetailResponse {
    private String title;
    private String description;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int bookmarkCount;
    private int contentCount;
}
