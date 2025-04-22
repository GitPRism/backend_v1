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

    private static final String REPOS_URL = "https://api.github.com/user/repos";

    /**
     * ✅ GitHub ID 기반으로 참여한 레포를 조회 (contributor 기반)
     */
    @Transactional
    public List<GitHubRepoResponse> getParticipatedRepositoriesByGithubId(String githubId) {
        GitHubUser user = gitHubUserRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 GitHub ID의 유저가 없습니다: " + githubId));

        return getParticipatedRepositories(user);
    }

    private List<GitHubRepoResponse> getParticipatedRepositories(GitHubUser user) {
        String accessToken = user.getAccessToken();
        String username = user.getUsername();
        OkHttpClient client = new OkHttpClient();

        Set<Long> processedRepoIds = new HashSet<>();
        List<Repo> savedRepos = new ArrayList<>();

        int page = 1;
        while (true) {
            String pagedUrl = REPOS_URL + "?affiliation=owner,collaborator,organization_member&per_page=100&page=" + page;

            Request request = new Request.Builder()
                    .url(pagedUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("레포 목록 조회 실패 (page {}): {}", page, response);
                    break;
                }

                String responseBody = response.body().string();
                JsonNode repoArray = objectMapper.readTree(responseBody);
                if (!repoArray.isArray() || repoArray.isEmpty()) break;

                for (JsonNode repoData : repoArray) {
                    Long githubRepoId = repoData.get("id").asLong();
                    if (processedRepoIds.contains(githubRepoId)) continue;

                    String repoName = repoData.get("name").asText();
                    String owner = repoData.get("owner").get("login").asText();
                    String contributorsUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/contributors";

                    Request contribRequest = new Request.Builder()
                            .url(contributorsUrl)
                            .header("Authorization", "Bearer " + accessToken)
                            .build();

                    try (Response contribResponse = client.newCall(contribRequest).execute()) {
                        if (!contribResponse.isSuccessful()) continue;

                        JsonNode contributors = objectMapper.readTree(contribResponse.body().string());
                        if (!contributors.isArray()) continue;

                        boolean isContributor = false;
                        for (JsonNode contributor : contributors) {
                            String contributorLogin = contributor.get("login").asText();
                            if (contributorLogin.equals(username)) {
                                isContributor = true;
                                break;
                            }
                        }

                        if (isContributor) {
                            processedRepoIds.add(githubRepoId);
                            savedRepos.add(parseAndSaveRepo(repoData, user));
                        }
                    } catch (IOException e) {
                        log.warn("contributors 조회 실패 for {}/{}: {}", owner, repoName, e.getMessage());
                    }
                }

                page++;
            } catch (IOException e) {
                throw new RuntimeException("레포 조회 실패 (page " + page + ")", e);
            }
        }

        return GitHubRepoResponse.fromEntities(savedRepos);
    }

    private Repo parseAndSaveRepo(JsonNode repoData, GitHubUser user) {
        Long githubRepoId = repoData.get("id").asLong();
        String name = repoData.get("name").asText();
        String description = repoData.hasNonNull("description") ? repoData.get("description").asText() : null;
        String url = repoData.get("html_url").asText();
        String visibility = repoData.get("private").asBoolean() ? "private" : "public";
        String defaultBranch = repoData.get("default_branch").asText();
        String language = repoData.hasNonNull("language") ? repoData.get("language").asText() : null;

        Optional<Repo> existingRepo = repoRepository.findByGithubRepoId(githubRepoId);
        Repo repo = existingRepo.orElse(Repo.builder().githubRepoId(githubRepoId).build());

        repo.setRepoName(name);
        repo.setGitId(user.getUsername());
        repo.setGithubId(user.getGithubId());
        repo.setDescription(description);
        repo.setUrl(url);
        repo.setVisibility(visibility);
        repo.setDefaultBranch(defaultBranch);
        repo.setLanguage(language);

        return repoRepository.save(repo);
    }
}
