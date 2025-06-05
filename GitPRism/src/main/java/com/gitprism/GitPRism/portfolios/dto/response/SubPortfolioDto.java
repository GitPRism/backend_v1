package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubPortfolioDto {
    private Long portfolioId;
    private String title;
    private String description;
}
