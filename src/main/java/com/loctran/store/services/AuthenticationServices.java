package com.loctran.store.services;

import com.loctran.store.dtos.AuthenticationRequest;
import com.loctran.store.dtos.AuthenticationResponse;
import com.loctran.store.mappers.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServices {
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        var jwtToken = jwtService.issueToken(authenticationRequest.getEmail());

        return new AuthenticationResponse(jwtToken);
    }
}
