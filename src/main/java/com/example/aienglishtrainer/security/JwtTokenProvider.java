package com.example.aienglishtrainer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder()
                .decode(Base64.getEncoder().encodeToString(secretKeyString.getBytes()));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // 토큰에서 username 추출
    public String getUsername(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 파싱
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 만료 시간 반환 (밀리초)
    public long getExpirationTime() {
        return expirationTime;
    }
}