package com.loctran.store.services;

import com.loctran.store.dtos.AuthenticationRequest;
import com.loctran.store.dtos.AuthenticationResponse;
import com.loctran.store.entities.User;
import com.loctran.store.exceptions.UnauthorizedException;
import com.loctran.store.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthenticationServices {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        Map<String, Object> claims = setClaims(user);

        String accessToken = jwtService.generateToken(user.getId(), claims);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), claims);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(604800);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return new AuthenticationResponse(accessToken);
    }

    public AuthenticationResponse refresh(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        if (userId == null || !jwtService.isTokenValid(refreshToken, userId)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user =  userRepository.findById(userId).orElseThrow();
        Map<String, Object> claims = setClaims(user);

        String accessToken = jwtService.generateToken(user.getId(), claims);

        return  new AuthenticationResponse(accessToken);
    }

    private Map<String, Object> setClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());

        return claims;
    }
}
