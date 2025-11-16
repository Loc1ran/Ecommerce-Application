package com.loctran.store.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String issueToken(Long id) {
        return issueToken(id, null);
    }

    public String issueToken(Long id, Map<String, Object> claims) {
        return
                Jwts.builder()
                        .claims()
                        .add(claims)
                        .subject(id.toString())
                        .issuedAt(Date.from(Instant.now()))
                        .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                        .and()
                        .signWith(getSecretKey())
                        .compact();
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
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

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(Date.from(Instant.now()));
    }
}
