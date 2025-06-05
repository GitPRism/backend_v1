package com.gitprism.GitPRism.config.websocket;

import com.gitprism.GitPRism.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes
  ) {
    try {
      // 쿼리 파라미터에서 token 추출
      String query = request.getURI().getQuery(); // token=xxx
      if (query == null || !query.startsWith("token=")) {
        log.warn("🔒 WebSocket 연결 거부: JWT 토큰 누락");
        return false;
      }

      String token = query.substring("token=".length());

      // JWT 유효성 검사
      if (!jwtTokenProvider.validateToken(token)) {
        log.warn("🔒 WebSocket 연결 거부: JWT 토큰 유효하지 않음");
        return false;
      }

      // githubId 추출 및 Principal 설정
      String githubId = jwtTokenProvider.getGithubIdFromToken(token);
      attributes.put("principal", new StompPrincipal(githubId));
      log.info("✅ WebSocket JWT 인증 성공: {}", githubId);

      return true;
    } catch (Exception e) {
      log.error("❌ WebSocket Handshake 중 예외 발생", e);
      return false;
    }
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception
  ) {
    // 필요 시 후처리 가능
  }

  // 내부 클래스: Principal 구현체
  public static class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
