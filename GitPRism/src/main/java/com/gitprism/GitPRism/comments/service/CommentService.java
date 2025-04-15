package com.gitprism.GitPRism.comments.service;

import com.gitprism.GitPRism.comments.dto.CommentRequestDto;
import com.gitprism.GitPRism.comments.dto.CommentResponseDto;
import com.gitprism.GitPRism.comments.entity.Comment;
import com.gitprism.GitPRism.comments.event.CommentCreatedEvent;
import com.gitprism.GitPRism.comments.repository.CommentRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final GitHubUserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * ✅ 댓글 생성
   */
  @Transactional
  public CommentResponseDto createComment(Long portfolioId, String githubId, CommentRequestDto requestDto) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("해당 GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    Comment comment = Comment.create(user, portfolio, requestDto.getComment());
    commentRepository.save(comment);

    // 📣 이벤트 발행
    eventPublisher.publishEvent(new CommentCreatedEvent(comment));

    return CommentResponseDto.of(comment, user.getUsername());
  }

  /**
   * ✅ 댓글 조회
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getCommentsByPortfolioId(Long portfolioId) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오가 존재하지 않습니다."));

    List<Comment> comments = commentRepository.findByPortfolioAndIsDeletedFalseOrderByCreatedAtDesc(portfolio);

    List<CommentResponseDto> commentDtoList = comments.stream()
        .map(comment -> CommentResponseDto.of(comment, comment.getUser().getUsername()))
        .toList();

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("message", "댓글 조회 성공");
    response.put("code", 200);
    response.put("portfolio_id", portfolioId);
    response.put("contentCount", commentDtoList.size());
    response.put("data", commentDtoList);

    return response;
  }
}
