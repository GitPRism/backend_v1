package com.gitprism.GitPRism.Repository.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.gitprism.GitPRism.Repository.dto.response.GitHubRepoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.gitprism.GitPRism.Repository.repository.RepoRepository;
import com.gitprism.GitPRism.Repository.entity.Repo;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubRepoService {

    public List<GitHubRepoResponse> getRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONArray jsonArray = new JSONArray(response);
        List<GitHubRepoResponse> repos = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject repo = jsonArray.getJSONObject(i);
            repos.add(new GitHubRepoResponse(
                    repo.getString("name"),
                    repo.getString("html_url"),
                    repo.optString("description", ""),
                    repo.optString("language", "")
            ));
        }

        return repos;
    }

    @Autowired
    private RepoRepository repoRepository;

    public List<GitHubRepoResponse> getPrivateRepositories(String accessToken) {
        String url = "https://api.github.com/user/repos";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONArray jsonArray = new JSONArray(response.getBody());
        List<GitHubRepoResponse> repos = new ArrayList<>();
        List<Repo> toSave = new ArrayList<>();

        // GitHub 로그인 유저 ID 가져오기
        ResponseEntity<String> userResponse = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                entity,
                String.class
        );
        JSONObject userJson = new JSONObject(userResponse.getBody());
        String gitId = userJson.getString("login");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject repo = jsonArray.getJSONObject(i);
            String repoName = repo.getString("name");

            GitHubRepoResponse dto = new GitHubRepoResponse(
                    repoName,
                    repo.getString("html_url"),
                    repo.optString("description", ""),
                    repo.optString("language", "")
            );
            repos.add(dto);

            Repo repoEntity = Repo.builder()
                    .gitId(gitId)
                    .repoName(repoName)
                    .url(repo.getString("html_url"))
                    .description(repo.optString("description", ""))
                    .defaultBranch(repo.optString("default_branch", "main"))
                    .language(repo.optString("language", ""))
                    .isDeleted(false)
                    .build();

            toSave.add(repoEntity);
        }

        repoRepository.saveAll(toSave); // ✅ DB 저장

        return repos;
    }

}
