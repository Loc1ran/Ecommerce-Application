package com.loctran.store.auth;

import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response){
        AuthenticationResponse authResponse = authenticationServices.login(request, response);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authResponse.getJwtToken()).body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ){
        AuthenticationResponse authResponse = authenticationServices.refresh(refreshToken);

        return ResponseEntity.ok().body(authResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
