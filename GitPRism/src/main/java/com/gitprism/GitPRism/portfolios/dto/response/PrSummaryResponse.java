package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrSummaryResponse {
    private String repoName;
    private List<PrSummary> summaries;
}
