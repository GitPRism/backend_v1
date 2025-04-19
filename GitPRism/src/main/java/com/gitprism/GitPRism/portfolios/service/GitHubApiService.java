package com.gitprism.GitPRism.portfolios.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import com.gitprism.GitPRism.portfolios.dto.response.GitHubPrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubApiService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<GitHubPrResponse> getUserPrsFromRepo(Repo repo, String accessToken) {
        String owner = extractOwnerFromUrl(repo.getUrl());
        String repoName = repo.getRepoName();

        String url = String.format("https://api.github.com/repos/%s/%s/pulls?state=all", owner, repoName);
        log.info("➡️ 최종 GitHub API URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("PR 목록 조회 실패: " + response.getStatusCode());
        }

        try {
            List<GitHubPrResponse> allPrs = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            String currentUser = getCurrentGitHubUsername(accessToken);

            return allPrs.stream()
                    .filter(pr -> currentUser.equals(pr.getUser().getLogin()))
                    .filter(pr -> isMerged(pr.getNumber(), repo, accessToken))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("PR 목록 파싱 실패", e);
        }
    }

    public String getPrDiff(Repo repo, int prNumber, String accessToken) {
        String owner = extractOwnerFromUrl(repo.getUrl());
        String repoName = repo.getRepoName();

        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/files", owner, repoName, prNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("PR diff 조회 실패: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private String getCurrentGitHubUsername(String accessToken) {
        String url = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("GitHub 사용자 정보 조회 실패: " + response.getStatusCode());
        }

        try {
            return objectMapper.readTree(response.getBody()).get("login").asText();
        } catch (Exception e) {
            throw new RuntimeException("GitHub 사용자 이름 파싱 실패", e);
        }
    }

    private String extractOwnerFromUrl(String url) {
        try {
            // 예: https://github.com/GitPRism/backend_v1 → "GitPRism"
            String[] parts = url.split("/");
            return parts[3]; // index 3 = owner
        } catch (Exception e) {
            throw new RuntimeException("레포 URL에서 owner 추출 실패: " + url);
        }
    }
    private boolean isMerged(int prNumber, Repo repo, String accessToken) {
        String owner = extractOwnerFromUrl(repo.getUrl());
        String repoName = repo.getRepoName();

        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d", owner, repoName, prNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return false;
            }
            String mergedAt = objectMapper.readTree(response.getBody()).path("merged_at").asText();
            return mergedAt != null && !mergedAt.equals("null");
        } catch (Exception e) {
            log.warn("PR #{} 병합 여부 확인 실패: {}", prNumber, e.getMessage());
            return false;
        }
    }

}
