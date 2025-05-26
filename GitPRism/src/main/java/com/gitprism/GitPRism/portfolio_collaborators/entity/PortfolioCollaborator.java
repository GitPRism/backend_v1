package com.gitprism.GitPRism.portfolio_collaborators.entity;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "portfolio_collaborators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCollaborator {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "portfolio_id")
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private GitHubUser user;

  @Enumerated(EnumType.STRING)
  private Role role;

  public enum Role {
    EDITOR, VIEWER
  }
}
