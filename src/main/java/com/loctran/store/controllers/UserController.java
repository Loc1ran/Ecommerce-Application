package com.loctran.store.controllers;

import com.loctran.store.dtos.CreateUserRequest;
import com.loctran.store.dtos.UpdatePasswordRequest;
import com.loctran.store.dtos.UpdateUserRequest;
import com.loctran.store.dtos.UserDTO;
import com.loctran.store.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + auth);
        System.out.println("Is authenticated: " + auth.isAuthenticated());
        System.out.println("Authorities: " + auth.getAuthorities());

        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.findUserById(id);

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest userCreateRequest) {
        UserDTO userDTO = userService.createUser(userCreateRequest);

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id")  Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        UserDTO userDTO = userService.updateUser(id, updateUserRequest);

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id")  Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> updatePassword(@PathVariable("id") Long id, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.updatePassword(id, updatePasswordRequest);

        return ResponseEntity.noContent().build();
    }
}
