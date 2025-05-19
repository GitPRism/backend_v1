package com.gitprism.GitPRism.portfolios.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PortfolioUpdateRequest {
    private String title;
    private String description;
}
