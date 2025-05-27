package com.gitprism.GitPRism.portfolio_collaborators.controller;

import com.gitprism.GitPRism.portfolio_collaborators.dto.request.AddCollaboratorRequest;
import com.gitprism.GitPRism.portfolio_collaborators.dto.response.PortfolioCollaboratorResponse;
import com.gitprism.GitPRism.portfolio_collaborators.entity.PortfolioCollaborator;
import com.gitprism.GitPRism.portfolio_collaborators.service.PortfolioCollaboratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolioId}/collaborators")
@Tag(name = "포트폴리오 협업자 API")
public class PortfolioCollaboratorController {

  private final PortfolioCollaboratorService collaboratorService;

  @PostMapping
  @Operation(summary = "협업자 추가", description = "포트폴리오에 협업자를 추가합니다.")
  public ResponseEntity<PortfolioCollaboratorResponse> addCollaborator(
      @PathVariable Long portfolioId,
      @RequestBody AddCollaboratorRequest request
  ) {
    if (collaboratorService.isAlreadyCollaborator(portfolioId, request.getUserId())) {
      return ResponseEntity.badRequest().body(
          new PortfolioCollaboratorResponse(
              portfolioId,
              request.getUserId(),
              request.getRole().name(),
              "이미 등록된 협업자입니다."
          )
      );
    }

    PortfolioCollaborator collaborator = collaboratorService.addCollaborator(
        portfolioId, request.getUserId(), request.getRole()
    );

    PortfolioCollaboratorResponse response = new PortfolioCollaboratorResponse(
        collaborator.getPortfolio().getId(),
        collaborator.getUser().getId(),
        collaborator.getRole().name(),
        "협업자가 성공적으로 등록되었습니다."
    );

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}/role")
  @Operation(summary = "협업자 권한 조회", description = "사용자가 해당 포트폴리오에 대해 어떤 권한을 가지고 있는지 조회합니다.")
  public ResponseEntity<String> getCollaboratorRole(
      @PathVariable Long portfolioId,
      @PathVariable Long userId
  ) {
    return collaboratorService
        .getCollaboratorRole(portfolioId, userId)
        .map(role -> ResponseEntity.ok(role.name()))
        .orElse(ResponseEntity.notFound().build());
  }
}
