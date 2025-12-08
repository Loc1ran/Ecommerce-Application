package com.loctran.store.jwts;

import com.loctran.store.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public Long getUserIdFromToken() {
        return Long.valueOf(claims.getSubject());
    }

    public String getEmailFromToken() {
        return claims.get("email").toString();
    }

    public Role getRoleFromToken(){
        return Role.valueOf(claims.get("role").toString());
    }

    public boolean isTokenValid(Long id) {
        Long subject = Long.valueOf(claims.getSubject());
        return subject.equals(id) && !isTokenExpired();
    }

    private boolean isTokenExpired() {
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    @Override
    public String toString() {
        return
                Jwts
                        .builder()
                        .claims(claims).signWith(secretKey).compact();
    }

}
