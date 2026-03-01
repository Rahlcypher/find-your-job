package com.fij.controllers;

import com.fij.dto.ProfileRequest;
import com.fij.dto.ProfileResponse;
import com.fij.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody ProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
}
