package com.fij.dto;

public record JobPreferenceRequest(
    Integer minSalary,
    String mobilityZone,
    String jobType,
    String workSchedule,
    String remotePreference
) {}
