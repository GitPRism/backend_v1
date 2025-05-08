package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioResponse {
    private String message;
    private int code;
    private Long id;
    private Map<String, String> data;
}
