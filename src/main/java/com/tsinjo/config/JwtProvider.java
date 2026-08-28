package com.tsinjo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtProvider {
    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .collect(Collectors.joining(","));
        Date now = new Date();
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .claim("authorities", authorities)
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String tokenOrHeader) {
        String token = extractToken(tokenOrHeader);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromJwtToken(String tokenOrHeader) {
        return parseClaims(tokenOrHeader).getSubject();
    }

    private String extractToken(String tokenOrHeader) {
        if (tokenOrHeader == null || tokenOrHeader.isBlank()) {
            throw new IllegalArgumentException("JWT is missing");
        }
        return tokenOrHeader.startsWith(JwtConstant.BEARER_PREFIX)
                ? tokenOrHeader.substring(JwtConstant.BEARER_PREFIX.length()) : tokenOrHeader;
    }
}
