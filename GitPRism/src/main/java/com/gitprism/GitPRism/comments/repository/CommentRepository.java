package com.gitprism.GitPRism.comments.repository;

import com.gitprism.GitPRism.comments.entity.Comment;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  int countByPortfolioAndIsDeletedFalse(Portfolio portfolio);
  List<Comment> findByPortfolioAndIsDeletedFalseOrderByCreatedAtDesc(Portfolio portfolio);
}
