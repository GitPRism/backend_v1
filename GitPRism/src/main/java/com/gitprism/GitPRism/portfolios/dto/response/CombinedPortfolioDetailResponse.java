package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedPortfolioDetailResponse {

    private Long portfolioId;
    private String username;
    private String avatarUrl;
    private String repoOrgAvatarUrl;
    private String title;

    private List<SubPortfolioDto> data;

    private LocalDateTime updatedAt;
    private int likeCount;
    private int bookmarkCount;
    private int contentCount;
    private boolean bookmarked;
    private boolean liked;
}
