package com.fij.dto;

import java.time.LocalDate;

public record ExperienceRequest(
    String title,
    String company,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    boolean currentJob
) {}
