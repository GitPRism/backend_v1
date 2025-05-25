package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.bookmarks.repository.BookmarkRepository;
import com.gitprism.GitPRism.comments.repository.CommentRepository;
import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.service.GitHubUserService;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import com.gitprism.GitPRism.likes.repository.LikeRepository;
import com.gitprism.GitPRism.portfolios.dto.request.PortfolioUpdateRequest;
import com.gitprism.GitPRism.portfolios.dto.response.*;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.entity.Portfolio.Status;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import com.gitprism.GitPRism.portfolios.search.PortfolioSearchIndexer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioSearchIndexer portfolioSearchIndexer;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final PortfolioRepository portfolioRepository;
    private final GitHubUserService gitHubUserService;
    private final RepoRepository repoRepository;
    private final GitHubApiService gitHubApiService;
    private final OpenAiService openAiService;


    public PortfolioResponse createPortfolio(Long userId, Long repoId,  Portfolio parentPortfolio) {
        GitHubUserResponseDto userDto = gitHubUserService.findById(userId);
        String accessToken = userDto.getAccessToken();
        Repo repo = repoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("레포를 찾을 수 없습니다."));

        var userPrs = gitHubApiService.getUserPrsFromRepo(repo, accessToken);
        var summaries = userPrs.stream()
                .map(pr -> {
                    String diff = gitHubApiService.getPrDiff(repo, pr.getNumber(), accessToken);
                    return openAiService.summarizePr(pr.getTitle(), pr.getBody(), diff);
                }).toList();

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

        var gptResult = openAiService.generatePortfolioDescription(prompt);
        GitHubUser user = gitHubUserService.findEntityById(userId);

        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .repo(repo)
                .title(gptResult.get("title"))
                .description(gptResult.get("description"))
                .status(Status.valueOf(gptResult.get("status").toUpperCase()))
                .repoOrgAvatarUrl(repo.getOrgAvatarUrl())
                .parent(parentPortfolio)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        portfolioRepository.save(portfolio);
        portfolioSearchIndexer.index(portfolio);
        PortfolioDetailDto detail = PortfolioDetailDto.builder()
                .portfolioId(portfolio.getId())
                .repoName(repo.getRepoName())
                .repoUrl(repo.getUrl())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .repoOrgAvatarUrl(repo.getOrgAvatarUrl())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .status(portfolio.getStatus().name().toLowerCase())
                .createdAt(portfolio.getCreatedAt())
                .bookmarked(false)
                .liked(false)
                .likeCount(0)
                .bookmarkCount(0)
                .commentCount(0)
                .build();

        return PortfolioResponse.builder()
                .message("포트폴리오 생성 완료")
                .code(201)
                .id(portfolio.getId())
                .data(detail)
                .build();
    }

    public PortfolioBatchResponse createBatch(Long userId, List<Long> repoIds) {
        List<PortfolioDetailDto> results = new ArrayList<>();

        Portfolio combined = createCombinedPortfolio(userId, new ArrayList<>());

        for (Long repoId : repoIds) {
            try {
                PortfolioResponse created = createPortfolio(userId, repoId, combined);
                results.add(created.getData());
            } catch (Exception e) {
                log.warn("레포 {} 처리 중 오류 발생: {}", repoId, e.getMessage());
            }
        }

        updateCombinedPortfolioFromDetails(combined, results);

        String representativeImageUrl = results.isEmpty() ? null : results.get(0).getAvatarUrl();

        return new PortfolioBatchResponse(
                "포트폴리오 %d개 생성 완료".formatted(results.size()),
                combined.getId(),
                representativeImageUrl,
                201,
                results.size(),
                results

        );
    }

    private void updateCombinedPortfolioFromDetails(Portfolio combined, List<PortfolioDetailDto> details) {
        if (details.isEmpty()) return;

        String summaryTitle = "요약 포트폴리오 (%d개 레포)".formatted(details.size());
        String summaryDescription = details.stream()
                .map(dto -> "- " + dto.getTitle() + ": " + dto.getDescription())
                .collect(Collectors.joining("\n\n"));

        combined.setTitle(summaryTitle);
        combined.setDescription(summaryDescription);
        combined.setUpdatedAt(LocalDateTime.now());
        portfolioRepository.save(combined);
        portfolioSearchIndexer.index(combined); // 추가
    }


    public PortfolioDetailResponse getPortfolioDetail(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오가 존재하지 않습니다."));

        if (!portfolio.getStatus().equals(Status.PUBLISHED)) {
            throw new IllegalStateException("해당 포트폴리오는 아직 공개되지 않았습니다.");
        }

        GitHubUser user = gitHubUserService.findEntityById(userId);
        int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        int contentCount = commentRepository.countByPortfolioIdAndIsDeletedFalse(portfolioId);
        boolean bookmarked = bookmarkRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, portfolio);

        return PortfolioDetailResponse.builder()
                .portfolioId(portfolio.getId())
                .username(portfolio.getUser().getUsername())
                .avatarUrl(portfolio.getUser().getAvatarUrl())
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .updatedAt(portfolio.getUpdatedAt())
                .likeCount(likeCount)
                .bookmarkCount(bookmarkCount)
                .contentCount(contentCount)
                .bookmarked(bookmarked)
                .build();
    }

    public List<PortfolioDetailDto> getPortfoliosByUser(Long userId) {
        GitHubUser user = gitHubUserService.findEntityById(userId);
        List<Portfolio> portfolios = portfolioRepository.findByUserAndIsDeletedFalse(user);

        return portfolios.stream()
                .map(p -> PortfolioDetailDto.builder()
                        .portfolioId(p.getId())
                        .repoName(p.getRepo() != null ? p.getRepo().getRepoName() : null)
                        .repoUrl(p.getRepo() != null ? p.getRepo().getUrl() : null)
                        .username(p.getUser().getUsername())
                        .avatarUrl(p.getUser().getAvatarUrl())
                        .repoOrgAvatarUrl(
                                p.getRepo() != null && p.getRepo().getOrgAvatarUrl() != null
                                        ? p.getRepo().getOrgAvatarUrl()
                                        : p.getRepoOrgAvatarUrl()
                        )
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .status(p.getStatus().name().toLowerCase())
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();
    }

    public List<PortfolioDetailDto> getAllPublicPortfolios(Long userId) {
        GitHubUser user = gitHubUserService.findEntityById(userId);
        List<Portfolio> published = portfolioRepository.findByStatusAndIsDeletedFalse(Status.PUBLISHED);


        return published.stream()
                .map(p -> {
                    boolean bookmarked = bookmarkRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, p);
                    int likeCount = likeRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
                    int bookmarkCount = bookmarkRepository.countByPortfolioIdAndIsDeletedFalse(p.getId());
                    boolean liked = likeRepository.existsByUserAndPortfolioAndIsDeletedFalse(user, p);
                    return PortfolioDetailDto.builder()
                            .portfolioId(p.getId())
                            .username(p.getUser().getUsername())
                            .avatarUrl(p.getUser().getAvatarUrl())
                            .repoOrgAvatarUrl(
                                    p.getRepo() != null && p.getRepo().getOrgAvatarUrl() != null
                                            ? p.getRepo().getOrgAvatarUrl()
                                            : p.getRepoOrgAvatarUrl()
                            )
                            .title(p.getTitle())
                            .description(p.getDescription())
                            .status(p.getStatus().name().toLowerCase())
                            .createdAt(p.getCreatedAt())
                            .bookmarked(bookmarked)
                            .liked(liked)
                            .likeCount(likeCount)
                            .bookmarkCount(bookmarkCount)
                            .build();
                })
                .toList();
    }

    public PortfolioResponse togglePortfolioStatus(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 포트폴리오를 수정할 수 없습니다.");
        }

        Status newStatus = (portfolio.getStatus() == Status.DRAFT)
                ? Status.PUBLISHED
                : Status.DRAFT;

        portfolio.setStatus(newStatus);
        portfolio.setUpdatedAt(LocalDateTime.now());

        portfolioRepository.save(portfolio);
        portfolioSearchIndexer.index(portfolio); // 추가
        String message = (newStatus == Status.PUBLISHED)
                ? "포트폴리오가 성공적으로 게시되었습니다."
                : "포트폴리오가 임시 저장되었습니다.";

        PortfolioDetailDto detail = PortfolioDetailDto.builder()
                .portfolioId(portfolio.getId())
                .repoName(portfolio.getRepo() != null ? portfolio.getRepo().getRepoName() : null)
                .repoUrl(portfolio.getRepo() != null ? portfolio.getRepo().getUrl() : null)
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .status(newStatus.name().toLowerCase())
                .createdAt(portfolio.getCreatedAt())
                .build();

        return PortfolioResponse.builder()
                .message(message)
                .code(200)
                .id(portfolio.getId())
                .data(detail)
                .build();
    }

    public Portfolio createCombinedPortfolio(Long userId, List<PortfolioDetailDto> detailList) {
        GitHubUser user = gitHubUserService.findEntityById(userId);
        StringBuilder fullDescription = new StringBuilder();

        String firstAvatarUrl = null;
        for (int i = 0; i < detailList.size(); i++) {
            PortfolioDetailDto dto = detailList.get(i);
            if (i == 0) {
                firstAvatarUrl = dto.getAvatarUrl();
            }

            fullDescription.append("""
                ▸ %s
                %s

                """.formatted(dto.getTitle(), dto.getDescription()));
        }

        Portfolio combined = Portfolio.builder()
                .user(user)
                .title("요약 포트폴리오 (%d개 레포)".formatted(detailList.size()))
                .description(fullDescription.toString())
                .repoOrgAvatarUrl(firstAvatarUrl)
                .status(Status.DRAFT)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        portfolioSearchIndexer.index(combined); // 추가
        return portfolioRepository.save(combined);
    }

    public PortfolioResponse updateIndividualPortfolio(Long userId, Long portfolioId, PortfolioUpdateRequest request) {
        Portfolio portfolio = portfolioRepository.findByIdAndIsDeletedFalse(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포트폴리오가 존재하지 않습니다."));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }


        boolean isCombinedPortfolio = (portfolio.getParent() == null);

        if (request.getTitle() != null) {
            portfolio.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            if (!isCombinedPortfolio) {
                portfolio.setDescription(request.getDescription());
            } else {
                log.warn("다중 포트폴리오에서는 description 수정이 제한됩니다. [portfolioId={}]", portfolioId);
            }
        }

        portfolio.setUpdatedAt(LocalDateTime.now());
        portfolioRepository.save(portfolio);
        portfolioSearchIndexer.index(portfolio); // 추가
        Portfolio parent = portfolio.getParent();
        if (parent != null) {
            List<Portfolio> children = portfolioRepository.findByParentId(parent.getId());

            String updatedDescription = children.stream()
                    .map(p -> "- " + p.getTitle() + ": " + p.getDescription())
                    .collect(Collectors.joining("\n\n"));

            parent.setDescription(updatedDescription);
            parent.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(parent);
            portfolioSearchIndexer.index(parent); // 추가
        }

        PortfolioDetailDto detail = PortfolioDetailDto.fromEntity(portfolio);
        return PortfolioResponse.builder()
                .message("개별 포트폴리오 수정 및 요약 포트폴리오 갱신 완료")
                .code(200)
                .id(portfolio.getId())
                .data(detail)
                .build();
    }

}

