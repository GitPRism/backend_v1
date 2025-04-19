package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrSummary {
    private String prTitle;
    private String prBody;
    private String summary;
    private String importantCode;
}
