package com.gitprism.GitPRism.github_users.service;

import com.gitprism.GitPRism.github_users.entity.GitHubUser;
import com.gitprism.GitPRism.github_users.repository.GitHubUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.gitprism.GitPRism.config.jwt.JwtTokenProvider;
import com.gitprism.GitPRism.github_users.dto.response.LoginResponseDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OAuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RestTemplate restTemplate;
  private final GitHubUserRepository userRepository;

  @Value("${github.client.id}")
  private String clientId;

  @Value("${github.client.secret}")
  private String clientSecret;

  @Value("${github.redirect.uri}")
  private String redirectUri;

  public String generateGitHubLoginUrl() {
    return String.format(
        "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user",
        clientId, redirectUri
    );
  }

  public String exchangeCodeForAccessToken(String code) {
    String url = "https://github.com/login/oauth/access_token";

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> body = new HashMap<>();
    body.put("client_id", clientId);
    body.put("client_secret", clientSecret);
    body.put("code", code);
    body.put("redirect_uri", redirectUri);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

    Map<String, Object> responseBody = response.getBody();
    System.out.println("GitHub 토큰 응답: " + responseBody); // ✅ 디버깅용 로그

    if (responseBody == null || responseBody.get("access_token") == null) {
      throw new RuntimeException("GitHub에서 access_token을 받지 못했습니다.");
    }

    return response.getBody().get("access_token").toString();
  }

  public LoginResponseDto loginWithGitHub(String code) {
    String accessToken = exchangeCodeForAccessToken(code);

    // GitHub 사용자 정보 요청
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken); // Authorization: Bearer <token>
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        "https://api.github.com/user",
        HttpMethod.GET,
        entity,
        Map.class
    );

    Map<String, Object> userInfo = response.getBody();

    String githubId = String.valueOf(userInfo.get("id"));
    String username = (String) userInfo.get("login");
    String email = (String) userInfo.get("email"); // null일 수 있음

    // 이미 존재하면 업데이트, 없으면 생성
    GitHubUser user = userRepository.findByGithubId(githubId)
        .map(existing -> {
          existing.setUsername(username);
          existing.setEmail(email);
          existing.setAccessToken(accessToken);
          return existing;
        })
        .orElse(GitHubUser.builder()
            .githubId(githubId)
            .username(username)
            .email(email)
            .accessToken(accessToken)
            .build());

    userRepository.save(user);
    // JWT 발급
    String jwt = jwtTokenProvider.createToken(user.getGithubId());

    // LoginResponseDto 반환
    return LoginResponseDto.builder()
        .token(jwt)
        .githubId(user.getGithubId())
        .username(user.getUsername())
        .email(user.getEmail())
        .build();
  }
}
