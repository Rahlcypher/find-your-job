package com.fij.controllers;

import com.fij.dto.AuthResponse;
import com.fij.dto.LoginRequest;
import com.fij.dto.RegisterRequest;
import com.fij.dto.RefreshTokenRequest;
import com.fij.dto.UpdateProfileRequest;
import com.fij.models.User;
import com.fij.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }
}
