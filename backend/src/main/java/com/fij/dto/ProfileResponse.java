package com.fij.dto;

import com.fij.models.Role;
import java.util.Set;

public record ProfileResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phone,
    String location,
    Set<Role> roles
) {}
