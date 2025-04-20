package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.service.GitHubUserService;
import com.gitprism.GitPRism.gitpullrequests.repository.PullRequestRepository;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import com.gitprism.GitPRism.portfolios.dto.response.PortfolioResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PrSummary;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.entity.Portfolio.Status;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final GitHubUserService gitHubUserService;
    private final RepoRepository repoRepository;
    private final PullRequestRepository pullRequestRepository;
    private final GitHubApiService gitHubApiService;
    private final OpenAiService openAiService;

    public PortfolioResponse createPortfolio(Long userId, Long repoId) {
        // 1. 유저 정보 조회
        GitHubUserResponseDto userDto = gitHubUserService.findById(userId);
        String accessToken = userDto.getAccessToken();
        String username = userDto.getUsername();

        // 2. 레포 정보 확인
        Repo repo = repoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("레포를 찾을 수 없습니다."));

        // 3. PR 목록 가져오기 (병합된 PR만)
        var userPrs = gitHubApiService.getUserPrsFromRepo(repo, accessToken);

        // 4. 각 PR 요약 분석 (OpenAI)
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

        // 6. Portfolio DB 저장
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

        // 7. 응답 반환
        return PortfolioResponse.builder()
                .message("포트폴리오 생성 완료")
                .code(201)
                .id(portfolio.getId())
                .data(gptResult)
                .build();
    }
}
