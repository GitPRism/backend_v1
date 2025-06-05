package com.gitprism.GitPRism.config;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import com.gitprism.GitPRism.portfolios.entity.Portfolio;
import com.gitprism.GitPRism.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MockDataLoader implements CommandLineRunner {

    private final GitHubUserRepository gitHubUserRepository;
    private final PortfolioRepository portfolioRepository;
    private final RepoRepository repoRepository;


    @Override
    public void run(String... args) {
        if (portfolioRepository.count() > 0) {
            System.out.println("Mock 데이터 이미 존재함. 생략.");
            return;
        }
        portfolioRepository.deleteAll();
        repoRepository.deleteAll();
        gitHubUserRepository.deleteAll();

        GitHubUser user1 = gitHubUserRepository.save(
                GitHubUser.builder()
                        .githubId("12345678")
                        .username("hong_dev")
                        .email("hong_dev@example.com")
                        .accessToken("ghp_mockToken1")
                        .avatarUrl("https://avatars.githubusercontent.com/u/11111111?v=4")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deleted(false)
                        .build()
        );

        GitHubUser user2 = gitHubUserRepository.save(
                GitHubUser.builder()
                        .githubId("23456789")
                        .username("yeji_log")
                        .email("yeji_log@example.com")
                        .accessToken("ghp_mockToken2")
                        .avatarUrl("https://avatars.githubusercontent.com/u/22222222?v=4")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deleted(false)
                        .build()
        );

        GitHubUser user3 = gitHubUserRepository.save(
                GitHubUser.builder()
                        .githubId("34567890")
                        .username("james_code")
                        .email("james_code@example.com")
                        .accessToken("ghp_mockToken3")
                        .avatarUrl("https://avatars.githubusercontent.com/u/33333333?v=4")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deleted(false)
                        .build()
        );

        Repo mockRepo = repoRepository.save(
                Repo.builder()
                        .repoName("mock-repo")
                        .orgAvatarUrl("https://avatars.githubusercontent.com/u/999001?v=4")
                        .description("Mock repo for testing")
                        .language("Java")
                        .defaultBranch("main")
                        .visibility("public")
                        .url("https://github.com/mock-org/mock-repo")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        System.out.println("✅ repo 저장됨 ID: " + mockRepo.getId());

        Portfolio p1 = Portfolio.builder()
                .user(user1)
                .repo(mockRepo)
                .title("Git 포트폴리오 관리 공유 프로젝트")
                .description("이 프로젝트는 GitHub 활동 기반의 포트폴리오를 자동 생성하고 공유하는 기능을 제공합니다.")
                .status(Portfolio.Status.PUBLISHED)
                .repoOrgAvatarUrl(mockRepo.getOrgAvatarUrl())
                .isDeleted(false)
                .createdAt(LocalDateTime.of(2025, 4, 10, 14, 12))
                .updatedAt(LocalDateTime.of(2025, 4, 10, 14, 12))
                .build();

        Portfolio p2 = Portfolio.builder()
                .user(user2)
                .repo(mockRepo)
                .title("개발자 일일 회고 자동 기록 서비스")
                .description("매일 자동으로 회고를 기록하고 통계화해주는 서비스입니다.")
                .status(Portfolio.Status.PUBLISHED)
                .repoOrgAvatarUrl(mockRepo.getOrgAvatarUrl())
                .isDeleted(false)
                .createdAt(LocalDateTime.of(2025, 4, 9, 10, 30))
                .updatedAt(LocalDateTime.of(2025, 4, 9, 10, 30))
                .build();

        Portfolio p3 = Portfolio.builder()
                .user(user3)
                .repo(mockRepo)
                .title("PR 분석 기반 코드 리뷰 피드백 시스템")
                .description("OpenAI를 활용하여 PR을 분석하고 자동 피드백을 생성합니다.")
                .status(Portfolio.Status.PUBLISHED)
                .repoOrgAvatarUrl(mockRepo.getOrgAvatarUrl())
                .isDeleted(false)
                .createdAt(LocalDateTime.of(2025, 4, 8, 16, 45))
                .updatedAt(LocalDateTime.of(2025, 4, 8, 16, 45))
                .build();

        portfolioRepository.saveAll(List.of(p1, p2, p3));
        System.out.println("📦 목업 유저 및 포트폴리오 생성 완료!");
    }
}