package com.gitprism.GitPRism.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.gitprism.GitPRism.config.websocket.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 클라이언트가 접속할 엔드포인트
    registry.addEndpoint("/ws/edit")
        .setHandshakeHandler(handshakeHandler())
        .addInterceptors(jwtHandshakeInterceptor) // ✅ JWT 인터셉터 추가
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 클라이언트가 구독할 주소(prefix)
    registry.enableSimpleBroker("/topic");
    // 클라이언트가 메시지를 보낼 때 prefix
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Bean
  public DefaultHandshakeHandler handshakeHandler() {
    return new DefaultHandshakeHandler() {
      @Override
      protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Object principal = attributes.get("principal"); // Interceptor에서 넣은 principal 꺼냄
        if (principal instanceof Principal) {
          return (Principal) principal;
        }
        return null;
      }
    };
  }

}
