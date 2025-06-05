package com.gitprism.GitPRism.portfolios.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioResponse {

    private String message;
    private int code;

    private Long id;  // 개별 포트폴리오 조회일 때만 사용
    private PortfolioDetailDto data;  // 단일 조회용

    //다중 조회일 때 사용
    private List<PortfolioDetailDto> content;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;
    private Boolean hasNext;

    // 생성자, builder 등은 목적별로 구분해서 생성해도 좋음
}

