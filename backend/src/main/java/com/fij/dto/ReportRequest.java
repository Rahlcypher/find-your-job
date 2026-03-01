package com.fij.dto;

public record ReportRequest(
    Long reportedUserId,
    String reason,
    String description
) {}
