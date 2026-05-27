package com.example.demo.user.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.user.dto.CreateUserRequest;
import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.model.User;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("service is running", Map.of("status", "UP"));
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success("query success", userService.list());
    }

    @GetMapping("/users/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        return ApiResponse.success("query success", userService.getById(id));
    }

    @PostMapping("/users")
    public ApiResponse<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success("create success", userService.create(request));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success("update success", userService.update(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<User> deleteUser(@PathVariable Long id) {
        return ApiResponse.success("delete success", userService.delete(id));
    }
}
