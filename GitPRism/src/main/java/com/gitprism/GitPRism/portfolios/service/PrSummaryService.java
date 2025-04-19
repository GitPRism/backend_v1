package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.github_users.dto.response.GitHubUserResponseDto;
import com.gitprism.GitPRism.github_users.service.GitHubUserService;
import com.gitprism.GitPRism.portfolios.dto.response.PrSummaryResponse;
import com.gitprism.GitPRism.portfolios.dto.response.PrSummary;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrSummaryService {

    private final GitHubUserService gitHubUserService;
    private final RepoRepository repoRepository;
    private final GitHubApiService gitHubApiService;
    private final OpenAiService openAiService;

    public PrSummaryResponse summarizeRepository(Long userId, Long repoId) {
        // 사용자 accessToken 조회 (삭제된 사용자 제외)
        GitHubUserResponseDto userDto = gitHubUserService.findById(userId);
        String accessToken = userDto.getAccessToken();

        //  레포 정보 확인
        Repo repo = repoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 레포가 존재하지 않습니다."));

        //  사용자가 생성한 PR 목록 가져오기
        var userPrs = gitHubApiService.getUserPrsFromRepo(repo, accessToken);

        //  각 PR에 대해 OpenAI 분석 수행
        List<PrSummary> summaries = userPrs.stream()
                .map(pr -> {
                    String diff = gitHubApiService.getPrDiff(repo, pr.getNumber(), accessToken);
                    log.info("📄 PR #{} diff 내용:\n{}", pr.getNumber(), diff);
                    return openAiService.summarizePr(pr.getTitle(), pr.getBody(), diff);
                })
                .toList();

        //  결과 반환
        return new PrSummaryResponse(repo.getRepoName(), summaries);
    }
}
