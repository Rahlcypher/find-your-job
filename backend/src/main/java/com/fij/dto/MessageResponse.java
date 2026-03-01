package com.fij.dto;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Long chatId,
    String senderEmail,
    String senderFirstName,
    String content,
    String attachmentUrl,
    String attachmentName,
    LocalDateTime sentAt,
    boolean read
) {}
