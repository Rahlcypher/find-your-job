package com.fij.dto;

public record UpdateProfileRequest(
    String firstName,
    String lastName,
    String phone,
    String location
) {}
