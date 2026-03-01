package com.fij.controllers;

import com.fij.dto.*;
import com.fij.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getMyChats() {
        return ResponseEntity.ok(chatService.getMyChats());
    }

    @PostMapping("/job/{jobId}")
    public ResponseEntity<ChatResponse> getOrCreateChat(@PathVariable Long jobId) {
        return ResponseEntity.ok(chatService.getOrCreateChat(jobId));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.getMessages(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long chatId,
            @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(chatId, request));
    }

    @PatchMapping("/{chatId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long chatId) {
        chatService.markAsRead(chatId);
        return ResponseEntity.ok().build();
    }
}
