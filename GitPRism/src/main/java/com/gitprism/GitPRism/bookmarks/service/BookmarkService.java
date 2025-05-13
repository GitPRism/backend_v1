package com.gitprism.GitPRism.bookmarks.service;

import com.gitprism.GitPRism.bookmarks.entity.Bookmark;
import com.gitprism.GitPRism.bookmarks.repository.BookmarkRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.likes.repository.LikeRepository;
import com.gitprism.GitPRism.comments.repository.CommentRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import com.gitprism.GitPRism.bookmarks.event.BookmarkCreatedEvent;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkListResponse;
import com.gitprism.GitPRism.bookmarks.dto.BookmarkStatusResponse;
import com.gitprism.GitPRism.portfolios.score.PortfolioScoreEvent;
import com.gitprism.GitPRism.portfolios.score.PortfolioScoreType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final GitHubUserRepository userRepository;
  private final PortfolioRepository portfolioRepository;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public Map<String, Object> addBookmark(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    Bookmark bookmark = bookmarkRepository.findByUserAndPortfolio(user, portfolio).orElse(null);

    if (bookmark != null) {
      if (!bookmark.isDeleted()) {
        throw new IllegalStateException("이미 북마크한 포트폴리오입니다.");
      }
      bookmark.recover();
    } else {
      bookmark = Bookmark.create(user, portfolio);
      bookmarkRepository.save(bookmark);
    }

    eventPublisher.publishEvent(new BookmarkCreatedEvent(bookmark));

    eventPublisher.publishEvent(new PortfolioScoreEvent(portfolioId, PortfolioScoreType.BOOKMARK));
    return Map.of(
        "message", "북마크가 추가되었습니다.",
        "code", 201,
        "portfolio_id", portfolioId,
        "user_id", user.getId()
    );
  }

  @Transactional
  public Map<String, Object> cancelBookmark(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    Bookmark bookmark = bookmarkRepository.findByUserAndPortfolio(user, portfolio)
        .orElseThrow(() -> new NoSuchElementException("북마크 기록이 존재하지 않습니다."));

    if (bookmark.isDeleted()) {
      throw new IllegalStateException("이미 취소된 북마크입니다.");
    }

    bookmark.delete();

    return Map.of(
        "message", "북마크가 취소되었습니다.",
        "code", 200,
        "portfolio_id", portfolioId,
        "user_id", user.getId()
    );
  }

  @Transactional(readOnly = true)
  public BookmarkListResponse getMyBookmarks(String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    List<Bookmark> bookmarks = bookmarkRepository.findAllByUserAndIsDeletedFalseOrderByCreatedAtDesc(user);

    List<BookmarkListResponse.BookmarkSimpleDto> result = bookmarks.stream()
        .map(b -> {
          Portfolio p = b.getPortfolio();
          int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
          int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
          int commentCount = commentRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
          boolean liked = likeRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, p);


          return new BookmarkListResponse.BookmarkSimpleDto(
                  p.getId(),
                  p.getTitle(),
                  p.getDescription(),
                  p.getUser().getUsername(),
                  p.getUser().getAvatarUrl(),
                  p.getRepoOrgAvatarUrl(),
                  true,   // 북마크 리스트니까 무조건 true
                  liked,
                  likeCount,
                  bookmarkCount,
                  commentCount,
                  b.getCreatedAt()
          );
        })
        .toList();

    return new BookmarkListResponse("북마크 목록 조회 성공", 200, result);
  }

  @Transactional(readOnly = true)
  public BookmarkStatusResponse getBookmarkStatus(Long portfolioId, String githubId) {
    GitHubUser user = userRepository.findByGithubId(githubId)
        .orElseThrow(() -> new NoSuchElementException("GitHub 사용자를 찾을 수 없습니다."));

    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoSuchElementException("포트폴리오를 찾을 수 없습니다."));

    boolean bookmarked = bookmarkRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, portfolio);

    return new BookmarkStatusResponse("북마크 여부 조회 성공", 200, portfolioId, bookmarked);
  }

}
