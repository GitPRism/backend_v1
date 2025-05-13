package com.gitprism.GitPRism.portfolios.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class PortfolioBatchRequest {
    private List<Long> repoIds;
}
