package com.fij.dto;

public record ExperienceRequest(
    String title,
    String company,
    String description,
    String startDate,
    String endDate,
    boolean currentJob
) {}
