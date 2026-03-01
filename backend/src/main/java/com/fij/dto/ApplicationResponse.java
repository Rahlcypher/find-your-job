package com.fij.dto;

import java.time.LocalDateTime;

public record ApplicationResponse(
    Long id,
    Long jobId,
    String jobTitle,
    Long candidateId,
    String candidateName,
    String status,
    String coverLetter,
    LocalDateTime appliedAt
) {}
