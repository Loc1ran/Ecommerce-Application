package com.loctran.store.services;

import com.loctran.store.dtos.AuthenticationRequest;
import com.loctran.store.dtos.AuthenticationResponse;
import com.loctran.store.entities.User;
import com.loctran.store.repositories.UserRepository;
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

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        Map<String,Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());

        String jwtToken = jwtService.issueToken(user.getId(), claims);

        return new AuthenticationResponse(jwtToken);
    }
}
