package com.gitprism.GitPRism.portfolios.controller;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import com.gitprism.GitPRism.portfolios.util.PortfolioEditDraftStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "포트폴리오 초안 관리", description = "Redis에 임시 저장된 포트폴리오 초안(title/description)을 조회하거나 저장합니다.")
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioDraftController {

  private final PortfolioEditDraftStore draftStore;
  private final PortfolioRepository portfolioRepository;

  @Operation(summary = "포트폴리오 초안 조회", description = "Redis에 저장된 포트폴리오의 title/description 초안 내용을 반환합니다.")
  @GetMapping("/{portfolioId}/draft")
  public Map<String, String> getDraft(
      @Parameter(description = "포트폴리오 ID", example = "5")
      @PathVariable Long portfolioId
  ) {
    String title = draftStore.getTitle(portfolioId);
    String description = draftStore.getDescription(portfolioId);

    Map<String, String> result = new HashMap<>();
    result.put("title", title != null ? title : "");
    result.put("description", description != null ? description : "");
    return result;
  }

  @Operation(summary = "초안 내용을 DB에 반영", description = "Redis에 저장된 title과 description을 실제 DB 포트폴리오 데이터에 반영합니다.")
  @PostMapping("/{portfolioId}/commit-draft")
  public ResponseEntity<Void> commitDraft(
      @Parameter(description = "포트폴리오 ID", example = "5")
      @PathVariable Long portfolioId
  ) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new IllegalArgumentException("포트폴리오 없음"));

    String title = draftStore.getTitle(portfolioId);
    String description = draftStore.getDescription(portfolioId);

    if (title != null && !title.isBlank()) portfolio.setTitle(title);
    if (description != null && !description.isBlank()) portfolio.setDescription(description);

    portfolioRepository.save(portfolio);
    return ResponseEntity.ok().build();
  }
}
