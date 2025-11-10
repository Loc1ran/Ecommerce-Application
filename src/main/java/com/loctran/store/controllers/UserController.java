package com.loctran.store.controllers;

import com.loctran.store.dtos.CreateUserRequest;
import com.loctran.store.dtos.UpdatePasswordRequest;
import com.loctran.store.dtos.UpdateUserRequest;
import com.loctran.store.dtos.UserDTO;
import com.loctran.store.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public List<UserDTO> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy
    ) {
        return userService.getAllUsers(sortBy);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody CreateUserRequest userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable("id")  Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(id, updateUserRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id")  Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}")
    public void updatePassword(@PathVariable("id") Long id, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.updatePassword(id, updatePasswordRequest);
    }
}
