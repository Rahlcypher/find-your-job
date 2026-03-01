package com.fij.dto;

public record ProfileRequest(
    String firstName,
    String lastName,
    String phone,
    String location
) {}
