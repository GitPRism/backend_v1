package com.gitprism.GitPRism.portfolios.repository;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
  List<Portfolio> findByIdIn(List<Long> ids);
  List<Portfolio> findAllByStatusAndIsDeletedFalse(Portfolio.Status status);
}
