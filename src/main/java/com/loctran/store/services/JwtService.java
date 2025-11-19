package com.loctran.store.services;

import com.loctran.store.config.JwtConfig;
import com.loctran.store.entities.Jwt;
import com.loctran.store.entities.Role;
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

    public Jwt generateToken(Long id, Map<String, Object> claims) {
        return generateToken(id, claims, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(Long id, Map<String, Object> claims) {
        return generateToken(id, claims, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(Long id, Map<String, Object> claims, long expirationDate) {
        Claims claim = Jwts.claims()
                .add(claims)
                .subject(id.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationDate * 1000L))
                .build();

        return new Jwt(claim, jwtConfig.getSecretKey());
    }

    public Jwt parseToken(String token) {
        try {
            var claims = getClaimsFromToken(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (Exception e) {
            return null;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
