package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.bookmarks.repository.BookmarkRepository;
import com.gitprism.GitPRism.comments.repository.CommentRepository;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.service.GitHubUserService;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import com.gitprism.GitPRism.likes.repository.LikeRepository;
import com.gitprism.GitPRism.portfolios.dto.response.*;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.entity.Portfolio.Status;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final PortfolioRepository portfolioRepository;
    private final GitHubUserService gitHubUserService;
    private final RepoRepository repoRepository;
    private final GitHubApiService gitHubApiService;
    private final OpenAiService openAiService;

    public PortfolioResponse createPortfolio(Long userId, Long repoId) {
        GitHubUserResponseDto userDto = gitHubUserService.findById(userId);
        String accessToken = userDto.getAccessToken();
        String username = userDto.getUsername();

        Repo repo = repoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("레포를 찾을 수 없습니다."));

        var userPrs = gitHubApiService.getUserPrsFromRepo(repo, accessToken);

        List<PrSummary> summaries = userPrs.stream()
                .map(pr -> {
                    String diff = gitHubApiService.getPrDiff(repo, pr.getNumber(), accessToken);
                    return openAiService.summarizePr(pr.getTitle(), pr.getBody(), diff);
                })
                .toList();
        log.info(" 총 {}개의 PR을 분석했습니다", summaries.size());
        for (int i = 0; i < summaries.size(); i++) {
            var s = summaries.get(i);
            log.info(" PR #{} 요약: {}", i + 1, s.getSummary());
            log.info("   ┗ 중요 코드: {}", s.getImportantCode());
        }

        StringBuilder stackList = new StringBuilder();
        StringBuilder formattedSummaries = new StringBuilder();

        for (PrSummary s : summaries) {
            stackList.append(s.getImportantCode()).append("\n");

            formattedSummaries.append("""
                    PR 제목: %s
                    PR 내용: %s
                    요약: %s
                    중요 코드 및 기술: %s

                    """.formatted(
                    s.getPrTitle(),
                    s.getPrBody(),
                    s.getSummary(),
                    s.getImportantCode()
            ));
        }

        String prompt = openAiService.buildPortfolioPrompt(
                stackList.toString(),
                formattedSummaries.toString()
        );
        log.info("GPT에 보낼 포트폴리오 생성 프롬프트:\n{}", prompt);

        var gptResult = openAiService.generatePortfolioDescription(prompt);
        com.gitprism.GitPRism.github_users.entity.GitHubUser user = gitHubUserService.findEntityById(userId);
        log.info(" GPT 응답 결과: {}", gptResult);
        log.info("포트폴리오 저장 내용 → title: {}, status: {}",
                gptResult.get("title"), gptResult.get("status"));

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .title(gptResult.get("title"))
                .description(gptResult.get("description"))
                .status(Status.valueOf(gptResult.get("status").toUpperCase()))
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        portfolioRepository.save(portfolio);

        PortfolioDetailDto detail = PortfolioDetailDto.builder()
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .status(portfolio.getStatus().name().toLowerCase())
                .viewCount(0)
                .likeCount(0)
                .bookmarkCount(0)
                .contentCount(0)
                .createdAt(portfolio.getCreatedAt())
                .build();

        return PortfolioResponse.builder()
                .message("포트폴리오 생성 완료")
                .code(201)
                .id(portfolio.getId())
                .data(detail)
                .build();
    }

    public List<PortfolioResponse> getPortfoliosByUser(Long userId) {
        com.gitprism.GitPRism.github_users.entity.GitHubUser user = gitHubUserService.findEntityById(userId);
        List<Portfolio> portfolioList = portfolioRepository.findByUserAndIsDeletedFalse(user);

        return portfolioList.stream()
                .map(portfolio -> {
                    PortfolioDetailDto detail = PortfolioDetailDto.builder()
                            .title(portfolio.getTitle())
                            .description(portfolio.getDescription())
                            .status(portfolio.getStatus().name().toLowerCase())
                            .viewCount(0)
                            .likeCount(0)
                            .bookmarkCount(0)
                            .contentCount(0)
                            .createdAt(portfolio.getCreatedAt())
                            .build();

                    return PortfolioResponse.builder()
                            .id(portfolio.getId())
                            .message("조회 성공")
                            .code(200)
                            .data(detail)
                            .build();
                })
                .toList();
    }

    public PortfolioDetailResponse getPortfolioDetail(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오가 존재하지 않습니다."));

        if (!portfolio.getStatus().equals(Portfolio.Status.PUBLISHED)) {
            throw new IllegalStateException("해당 포트폴리오는 아직 공개되지 않았습니다.");
        }

        int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int contentCount = commentRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId); // 댓글 수

        return PortfolioDetailResponse.builder()
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .updatedAt(portfolio.getUpdatedAt())
                .likeCount(likeCount)
                .bookmarkCount(bookmarkCount)
                .contentCount(contentCount)
                .build();
    }

    @Transactional
    public PortfolioResponse togglePortfolioStatus(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 포트폴리오를 수정할 수 없습니다.");
        }

        Portfolio.Status newStatus = (portfolio.getStatus() == Portfolio.Status.DRAFT)
                ? Portfolio.Status.PUBLISHED
                : Portfolio.Status.DRAFT;

        portfolio.setStatus(newStatus);
        portfolio.setUpdatedAt(LocalDateTime.now());

        int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int contentCount = commentRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);

        String message = (newStatus == Portfolio.Status.PUBLISHED)
                ? "포트폴리오가 성공적으로 게시되었습니다."
                : "포트폴리오가 임시 저장되었습니다.";

        PortfolioDetailDto detail = PortfolioDetailDto.builder()
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .status(newStatus.name().toLowerCase())
                .viewCount(0)
                .likeCount(likeCount)
                .bookmarkCount(bookmarkCount)
                .contentCount(contentCount)
                .createdAt(portfolio.getCreatedAt())
                .build();

        return PortfolioResponse.builder()
                .message(message)
                .code(200)
                .id(portfolio.getId())
                .data(detail)
                .build();
    }

    public List<PublicPortfolioResponse> getAllPublicPortfolios() {
        List<Portfolio> published = portfolioRepository.findByStatusAndIsDeletedFalse(Portfolio.Status.PUBLISHED);

        return published.stream()
                .map(p -> {
                    int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
                    int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
                    int commentCount = commentRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());

                    return PublicPortfolioResponse.builder()
                            .portfolioId(p.getId())
                            .title(p.getTitle())
                            .username(p.getUser().getUsername())
                            .status(p.getStatus().name().toLowerCase())
                            .created_at(p.getCreatedAt())
                            .likeCount(likeCount)
                            .bookmarkCount(bookmarkCount)
                            .commentCount(commentCount)
                            .build();
                }).toList();
    }


}