package com.fij.dto;

import java.time.LocalDateTime;

public record JobRequest(
    String title,
    String description,
    String company,
    String location,
    Integer salaryMin,
    Integer salaryMax,
    String jobType,
    String workSchedule,
    String remotePolicy,
    Integer duration
) {}
