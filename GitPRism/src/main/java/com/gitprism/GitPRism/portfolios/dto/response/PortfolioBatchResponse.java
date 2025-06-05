package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioBatchResponse {
    private String message;
    private Long combinedPortfolioId;
    private String representativeImageUrl;
    private int code;
    private int count;
    private int page;
    private int pageSize;
    private int totalPages;
    private long totalCount;
    private boolean hasNext;
    private List<PortfolioDetailDto> data;
}