package com.loctran.store.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotNull(message = "Email is required")
    @Email
    private String email;

    @NotNull(message = "password is required")
    private String password;
}
