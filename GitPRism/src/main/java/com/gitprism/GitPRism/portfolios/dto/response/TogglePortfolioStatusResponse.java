package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TogglePortfolioStatusResponse {
    private String message;
    private String status; // "PUBLISHED" 또는 "DRAFT"
}
