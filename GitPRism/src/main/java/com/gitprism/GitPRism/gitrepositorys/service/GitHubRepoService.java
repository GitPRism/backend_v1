package com.gitprism.GitPRism.gitrepositorys.service;

import com.gitprism.GitPRism.gitrepositorys.dto.response.GitHubRepoResponse;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepoService {

    private final GitHubUserRepository gitHubUserRepository;
    private final RepoRepository repoRepository;
    private final ObjectMapper objectMapper;

    private static final String GITHUB_API_URL = "https://api.github.com/user/repos";

    public String getAccessTokenByUserId(Long userId) {
        GitHubUser user = gitHubUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

        String accessToken = user.getAccessToken();
        return user.getAccessToken();
    }

    @Transactional
    public List<GitHubRepoResponse> getParticipatedRepositories(Long userId) {
        GitHubUser user = gitHubUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

        String accessToken = user.getAccessToken();
        String username = user.getUsername();

        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // 1. 유저 이벤트 호출
        Request request = new Request.Builder()
                .url("https://api.github.com/users/" + username + "/events")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("GitHub 이벤트 조회 실패: {}", response);
                throw new IOException("GitHub 이벤트 조회 실패: " + response);
            }

            JsonNode events = mapper.readTree(response.body().string());
            Set<Long> processedRepoIds = new HashSet<>();

            List<Repo> savedRepos = new ArrayList<>();
            for (JsonNode event : events) {
                String type = event.get("type").asText();
                if (!type.equals("PullRequestEvent") && !type.equals("IssuesEvent")) continue;

                String fullRepoName = event.get("repo").get("name").asText();


                // 2. 해당 레포 정보 호출
                Request repoRequest = new Request.Builder()
                        .url("https://api.github.com/repos/" + fullRepoName)
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

                try (Response repoResponse = client.newCall(repoRequest).execute()) {
                    if (!repoResponse.isSuccessful()) {
                        log.warn("레포 정보 조회 실패: {}", fullRepoName);
                        continue;
                    }

                    JsonNode repoData = mapper.readTree(repoResponse.body().string());

                    Long githubRepoId = repoData.get("id").asLong();
                    if (processedRepoIds.contains(githubRepoId)) {
                        continue;
                    }
                    processedRepoIds.add(githubRepoId);
                    String name = repoData.get("name").asText();
                    String githubId = repoData.get("id").asText();
                    String description = repoData.hasNonNull("description") ? repoData.get("description").asText() : null;
                    String url = repoData.get("html_url").asText();
                    String visibility = repoData.get("private").asBoolean() ? "private" : "public";
                    String defaultBranch = repoData.get("default_branch").asText();
                    String language = repoData.hasNonNull("language") ? repoData.get("language").asText() : null;


                    Optional<Repo> existingRepo = repoRepository.findByGithubRepoId(githubRepoId);
                    Repo repo = existingRepo.orElse(Repo.builder().githubRepoId(githubRepoId).build());

                    repo.setRepoName(name);
                    repo.setGitId(user.getUsername());        // ✅ git_id ← 유저네임 (예: john123)
                    repo.setGithubId(user.getGithubId());
                    repo.setDescription(description);
                    repo.setUrl(url);
                    repo.setVisibility(visibility);
                    repo.setDefaultBranch(defaultBranch);
                    repo.setLanguage(language);

                    savedRepos.add(repoRepository.save(repo));
                }
            }

            return GitHubRepoResponse.fromEntities(savedRepos);

        } catch (IOException e) {
            throw new RuntimeException("참여 레포 조회 실패", e);
        }
    }
}
