package com.gitprism.GitPRism.config;

import com.gitprism.GitPRism.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/api-docs/**",
                "/api/v1/github/**"
            ).permitAll()
            .requestMatchers("/api/v1/users/me").hasAuthority("ROLE_USER")
            .requestMatchers("/api/v1/github-users/**").hasAuthority("ROLE_USER")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
