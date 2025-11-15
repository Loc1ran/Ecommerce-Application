package com.loctran.store.controllers;

import com.loctran.store.dtos.AuthenticationRequest;
import com.loctran.store.dtos.AuthenticationResponse;
import com.loctran.store.services.AuthenticationServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private AuthenticationServices authenticationServices;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request){
        AuthenticationResponse response = authenticationServices.login(request);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, response.getJwtToken()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
