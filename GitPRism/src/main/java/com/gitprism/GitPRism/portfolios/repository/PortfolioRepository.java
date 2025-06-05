package com.gitprism.GitPRism.portfolios.repository;

import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import org.springframework.data.jpa.repository.Query;


public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
  // 인기 포트폴리오 id 목록 기반으로 실제 포트폴리오 데이터 조회
  List<Portfolio> findByIdIn(List<Long> ids);
  List<Portfolio> findByUserAndIsDeletedFalse(GitHubUser user, Pageable pageable);
  Optional<Portfolio> findByIdAndIsDeletedFalse(Long id);
  List<Portfolio> findByStatusAndIsDeletedFalse(Portfolio.Status status);
  List<Portfolio> findAllByStatusAndIsDeletedFalse(Portfolio.Status status);
  List<Portfolio> findByParentId(Long parentId);
  List<Portfolio> findByUserIdAndIsDeletedFalse(Long userId);
  @Query("SELECT p FROM Portfolio p WHERE p.status = 'PUBLISHED' AND p.isDeleted = false")
  Page<Portfolio> findPublicPortfoliosWithPaging(Pageable pageable);
  Page<Portfolio> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
  Page<Portfolio> findByUserIdAndIsDeletedFalseAndParentIsNull(Long userId, Pageable pageable);
}