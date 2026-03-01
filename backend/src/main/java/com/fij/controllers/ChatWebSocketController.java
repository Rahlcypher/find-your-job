package com.fij.controllers;

import com.fij.services.WebSocketChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final WebSocketChatService webSocketChatService;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload Map<String, Object> payload) {
        Long chatId = Long.parseLong(payload.get("chatId").toString());
        String content = payload.get("content").toString();
        
        webSocketChatService.handleSendMessage(chatId, content);
    }
}
