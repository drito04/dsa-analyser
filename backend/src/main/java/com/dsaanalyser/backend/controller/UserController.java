package com.dsaanalyser.backend.controller;

import com.dsaanalyser.backend.dto.AuthRequest;
import com.dsaanalyser.backend.dto.AuthResponse;
import com.dsaanalyser.backend.dto.RegisterRequest;
import com.dsaanalyser.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        AuthResponse authResponse = userService.register(registerRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", authResponse);
        response.put("message", "User registered successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody AuthRequest authRequest) {

        AuthResponse authResponse = userService.login(authRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", authResponse);
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }
}