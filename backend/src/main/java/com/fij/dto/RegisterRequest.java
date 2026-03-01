package com.fij.dto;

import com.fij.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phone,
    String location,
    @NotNull Set<Role> roles
) {}
