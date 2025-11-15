package com.loctran.store.services;

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

    public String issueToken(String email) {
        return issueToken(email, null);
    }

    public String issueToken(String email, Map<String, Object> claims) {
        return
                Jwts.builder()
                        .claims()
                        .add(claims)
                        .subject(email)
                        .issuedAt(Date.from(Instant.now()))
                        .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                        .and()
                        .signWith(getSecretKey())
                        .compact();
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
