package com.gitprism.GitPRism.portfolios.service;

import com.gitprism.GitPRism.portfolios.dto.response.PrSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public PrSummary summarizePr(String title, String body, String diff) {
        String prompt = buildPrompt(title, body, diff);

        // 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        // 요청 바디 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
                    String[] parts = content.split("\n", 2);
                    String summary = parts.length > 0 ? parts[0].trim() : "";
                    String importantCode = parts.length > 1 ? parts[1].trim() : "";

                    return new PrSummary(title, body, summary, importantCode);
                }
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }

        throw new RuntimeException("OpenAI 응답이 유효하지 않습니다.");
    }

    private String buildPrompt(String title, String body, String diff) {
        return """
    아래는 하나의 GitHub Pull Request입니다.

    [제목]
    %s

    [내용]
    %s

    [코드 변경]
    %s

    아래 내용을 기반으로 다음 정보를 구성해주세요:

    1. 이 PR의 주요 구현 내용을 3~4문장으로 요약해주세요.
    2. 핵심 변경이 적용된 코드 또는 클래스/파일명을 알려주세요.
    3. 사용된 기술 스택(예: Spring Boot, JPA, MySQL, Redis 등)을 예측하여 나열해주세요.

    출력 형식:
    - 요약: ...
    - 핵심 코드: ...
    - 기술 스택: ...
    """.formatted(title, body == null ? "(내용 없음)" : body, diff);
    }
}
