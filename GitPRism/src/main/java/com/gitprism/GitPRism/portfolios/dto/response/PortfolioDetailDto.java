package com.gitprism.GitPRism.portfolios.dto.response;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
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
    private String repoOrgAvatarUrl;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private boolean bookmarked;
    private boolean liked;
    private int likeCount;
    private int bookmarkCount;
    private int commentCount;
    private Long parentId;

    public static PortfolioDetailDto fromEntity(Portfolio entity) {
        return PortfolioDetailDto.builder()
                .portfolioId(entity.getId())
                .repoName(entity.getRepo() != null ? entity.getRepo().getRepoName() : null)
                .repoUrl(entity.getRepo() != null ? entity.getRepo().getUrl() : null)
                .username(entity.getUser().getUsername())
                .avatarUrl(entity.getUser().getAvatarUrl())
                .repoOrgAvatarUrl(entity.getRepoOrgAvatarUrl())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus().name().toLowerCase())
                .createdAt(entity.getCreatedAt())
                .bookmarked(false)
                .liked(false)
                .likeCount(0)
                .bookmarkCount(0)
                .commentCount(0)
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }
}
