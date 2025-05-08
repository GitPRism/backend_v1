package com.gitprism.GitPRism.portfolios.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitprism.GitPRism.portfolios.dto.response.PrSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // ✅ PR 분석 요약 프롬프트
    public PrSummary summarizePr(String title, String body, String diff) {
        String prompt = """
            아래는 GitHub Pull Request의 제목, 설명, 그리고 코드 변경(diff) 내용입니다.
            이를 바탕으로 변경의 요지를 요약하고, 중요 코드 또는 기술 스택이 드러나는 부분이 있다면 함께 알려주세요.

            출력 형식:
            {
              "summary": "...",
              "importantCode": "..."
            }

            PR 제목:
            %s

            PR 설명:
            %s

            코드 변경 내용 (diff):
            %s
            """.formatted(title, body, diff);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a professional software engineer helping write technical summaries."),
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

                    // JSON 파싱
                    Map<String, String> result = objectMapper.readValue(content, new TypeReference<>() {});
                    return new PrSummary(title, body, result.get("summary"), result.get("importantCode"));
                }
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("OpenAI 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + e.getMessage());
        }

        throw new RuntimeException("OpenAI 응답이 유효하지 않습니다.");
    }

    // 포트폴리오 생성용 프롬프트
    public String buildPortfolioPrompt(String stackList, String formattedSummaries) {
        return """
        당신은 사용자의 GitHub Pull Request 활동을 기반으로 포트폴리오 설명을 작성하는 AI입니다.
        아래 데이터를 바탕으로 하나의 포트폴리오를 구성해주세요.  
  
        출력 형식 예시 :
        
        {
          "title": "GitHub 기반 포트폴리오 생성기",
          "description": "[기술 스택]: React, TypeScript, TailwindCSS, Zustand\\n Tanstack-Query를 사용한 infinite Scroll 구현\\n -서버 데이터와 에러 처리를 선언적으로 관리할 수 있는 useInfiniteQuery를 사용하여  무한 스크롤 로직 단순화\\n 좋아요 기능을 useMutation을 사용해 Optimistic Update 구현\\n‘좋아요' 기능에 Optimistic UI 업데이트를 구현하여 서버 응답을 기다리지 않고 인터페이스 반응을 즉각적으로 반영하도록 구현\\n",
          "status": "draft"
        }

        아래는 사용자의 활동 요약입니다.
        이 내용을 참고하여 위 형식에 맞는 JSON 포트폴리오를 하나 생성해주세요.
        
        [기술 스택 및 코드 요소]
        %s
        
        [PR 활동 요약]
        %s
        """.formatted(stackList, formattedSummaries);
    }


    public Map<String, String> generatePortfolioDescription(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert portfolio assistant."),
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
                    log.warn("📦 GPT 응답 원문:\n{}", content);

                    // JSON 파싱
                    return objectMapper.readValue(content, new TypeReference<>() {});
                }
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + e.getMessage());
        }

        throw new RuntimeException("OpenAI 응답이 유효하지 않습니다.");
    }
}
