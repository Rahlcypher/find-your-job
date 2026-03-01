package com.fij.dto;

import java.time.LocalDate;

public record EducationRequest(
    String degree,
    String school,
    String fieldOfStudy,
    LocalDate startDate,
    LocalDate endDate
) {}
