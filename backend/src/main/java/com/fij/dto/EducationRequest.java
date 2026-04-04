package com.fij.dto;

public record EducationRequest(
    String degree,
    String school,
    String fieldOfStudy,
    String startDate,
    String endDate
) {}
