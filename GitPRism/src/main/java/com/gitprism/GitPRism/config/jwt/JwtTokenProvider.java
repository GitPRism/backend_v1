package com.gitprism.GitPRism.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKeyString;

  @Value("${jwt.expiration}")
  private long expirationTimeMs;

  private Key secretKey;

  @PostConstruct
  protected void init() {
    this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
  }

  /**
   * JWT 토큰 생성
   */
  public String createToken(String githubId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationTimeMs);

    return Jwts.builder()
        .setSubject(githubId)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * JWT에서 사용자 ID 추출
   */
  public String getGithubIdFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * JWT 유효성 검사
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
