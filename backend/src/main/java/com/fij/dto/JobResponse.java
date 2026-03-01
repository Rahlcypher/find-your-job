package com.fij.dto;

import java.time.LocalDateTime;

public record JobResponse(
    Long id,
    String title,
    String description,
    String company,
    String location,
    Integer salaryMin,
    Integer salaryMax,
    String jobType,
    String workSchedule,
    String remotePolicy,
    Integer duration,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    Long recruiterId,
    String recruiterName
) {}
