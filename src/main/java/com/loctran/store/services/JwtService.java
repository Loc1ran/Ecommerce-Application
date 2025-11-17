package com.loctran.store.services;

import com.loctran.store.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateToken(Long id, Map<String, Object> claims) {
        return generateToken(id, claims, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Long id, Map<String, Object> claims) {
        return generateToken(id, claims, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(Long id, Map<String, Object> claims, long expirationDate) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(id.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationDate * 1000L))
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaimsFromToken(token).getSubject());
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).get("email").toString();
    }

    public boolean isTokenValid(String token, Long id) {
        Long subject = Long.valueOf(getClaimsFromToken(token).getSubject());
        return subject.equals(id) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(Date.from(Instant.now()));
    }
}
