package com.gitprism.GitPRism.gitpullrequests.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.gitprism.GitPRism.gitpullrequests.dto.GitHubPrResponse;
import com.gitprism.GitPRism.gitpullrequests.dto.GitHubPrResponseWrapper;
import com.gitprism.GitPRism.gitpullrequests.entity.PullRequest;
import com.gitprism.GitPRism.gitpullrequests.repository.PullRequestRepository;
import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import com.gitprism.GitPRism.gitrepositorys.dto.response.GitHubRepoResponse;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.gitrepositorys.repository.RepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubPrService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final PullRequestRepository pullRequestRepository;
    private final GitHubUserRepository gitHubUserRepository;
    private final RepoRepository repoRepository;

    @Transactional
    public GitHubPrResponseWrapper getParticipatedRepositoriesByGithubId(String githubId) {
        GitHubUser user = gitHubUserRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 GitHub ID의 유저가 없습니다: " + githubId));

        return getPullRequests(user);
    }

    public GitHubPrResponseWrapper getPullRequests(GitHubUser user) {
        String accessToken = user.getAccessToken();
        String gitId = user.getGithubId();
        Long userId = user.getId();

        List<Repo> repos = repoRepository.findByGithubId(gitId);
        log.info("조회된 레포 수: {}, githubId: {}", repos.size(), gitId);
        List<GitHubPrResponse> allPrs = new ArrayList<>();

        // 3. 각 레포에서 PR 가져오기
        for (Repo repo : repos) {
            String repoName = repo.getRepoName();
            String owner = repo.getGitId();

            String apiUrl = repo.getUrl()
                    .replace("https://github.com", "https://api.github.com/repos") + "/pulls?state=all";
            log.info("PR 조회 URL: {}", apiUrl);


            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<JsonNode> response = restTemplate.exchange(
                        apiUrl, HttpMethod.GET, entity, JsonNode.class
                );

                for (JsonNode prNode : Objects.requireNonNull(response.getBody())) {
                    String prAuthor = prNode.get("user").get("login").asText();
                    if (!prAuthor.equals(user.getUsername())) continue;

                    long prId = prNode.get("id").asLong();
                    int number = prNode.get("number").asInt();
                    String title = prNode.get("title").asText();
                    String body = prNode.has("body") ? prNode.get("body").asText() : null;
                    boolean draft = prNode.get("draft").asBoolean();
                    String createdAtStr = prNode.get("created_at").asText();
                    String updatedAtStr = prNode.get("updated_at").asText();
                    String mergedAtStr = prNode.has("merged_at") && !prNode.get("merged_at").isNull()
                            ? prNode.get("merged_at").asText() : null;
                            // 👉 merge되지 않은 PR은 저장/응답에서 제외
                    if (mergedAtStr == null) continue;
                    String sha = prNode.get("head").get("sha").asText();
                    String sourceBranch = prNode.get("head").get("ref").asText();
                    String targetBranch = prNode.get("base").get("ref").asText();
                    String htmlUrl = prNode.get("html_url").asText();
                    String labels = StreamSupport.stream(prNode.get("labels").spliterator(), false)
                            .map(labelNode -> labelNode.get("name").asText())
                            .collect(Collectors.joining(","));

// 저장
                    Optional<PullRequest> existing = pullRequestRepository.findByGithubPrId(prId);
                    PullRequest pullRequest = existing.orElseGet(PullRequest::new);

                    pullRequest.setGithubPrId(prId); // ✅ GitHub PR ID 따로 설정
                    pullRequest.setNumber(number);
                    pullRequest.setPrTitle(title);
                    pullRequest.setPrDetails(body);
                    pullRequest.setDraft(draft);
                    pullRequest.setCreatedAt(OffsetDateTime.parse(createdAtStr).toLocalDateTime());
                    pullRequest.setUpdatedAt(OffsetDateTime.parse(updatedAtStr).toLocalDateTime());
                    pullRequest.setMergedAt(OffsetDateTime.parse(mergedAtStr).toLocalDateTime());
                    pullRequest.setAuthor(prAuthor);
                    pullRequest.setSha(sha);
                    pullRequest.setSourceBranch(sourceBranch);
                    pullRequest.setTargetBranch(targetBranch);
                    pullRequest.setHtmlUrl(htmlUrl);
                    pullRequest.setLabels(labels);
                    pullRequest.setRepoId(repo.getId());
                    pullRequestRepository.save(pullRequest);


// 클라이언트 응답용 DTO
                    allPrs.add(GitHubPrResponse.builder()
                            .id(prId)
                            .PR_title(title)
                            .PR_details(body)
                            .author(prAuthor)
                            .htmlUrl(htmlUrl)
                            .build());

                }
            } catch (Exception e) {
                log.warn("레포 '{}'의 PR 조회 실패: {}", repoName, e.getMessage());
            }
        }

        return GitHubPrResponseWrapper.builder()
                .message("PR 조회 완료")
                .code(200)
                .id(userId)
                .data(new GitHubPrResponseWrapper.Data(gitId, allPrs))
                .build();
    }
}
