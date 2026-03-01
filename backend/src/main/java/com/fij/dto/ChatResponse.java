package com.fij.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatResponse(
    Long id,
    Long jobId,
    String jobTitle,
    String otherUserEmail,
    String otherUserFirstName,
    String lastMessage,
    LocalDateTime lastMessageAt,
    int unreadCount
) {}
