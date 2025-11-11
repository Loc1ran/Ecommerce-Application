package com.loctran.store.dtos;

import com.loctran.store.validation.LowerCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less then 255 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    @LowerCase(message = "Email must be lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long")
    private String password;
}
