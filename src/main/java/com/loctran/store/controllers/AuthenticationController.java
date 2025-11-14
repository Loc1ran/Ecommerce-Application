package com.loctran.store.controllers;

import com.loctran.store.dtos.AuthenticationRequest;
import com.loctran.store.services.AuthenticationServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private AuthenticationServices aAuthenticationServices;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthenticationRequest request){
        aAuthenticationServices.login(request);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
