package com.gitprism.GitPRism.config.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpReq = (HttpServletRequest) request;
    String path = httpReq.getRequestURI();

    if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")
        || path.equals("/swagger-ui.html") || path.startsWith("/api-docs")
        || path.startsWith("/api/v1/github/oauth")) {
      chain.doFilter(request, response);
      return;
    }

    String authHeader = httpReq.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (!jwtTokenProvider.validateToken(token)) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
        return;
      }

      String githubId = jwtTokenProvider.getGithubIdFromToken(token);

      // 인증 객체 생성 후 SecurityContext에 등록
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              githubId,
              null,
              List.of(new SimpleGrantedAuthority("ROLE_USER"))
          );

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }
}
