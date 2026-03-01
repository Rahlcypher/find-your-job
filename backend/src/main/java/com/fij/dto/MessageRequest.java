package com.fij.dto;

import java.time.LocalDateTime;

public record MessageRequest(
    String content,
    String attachmentUrl,
    String attachmentName
) {}
