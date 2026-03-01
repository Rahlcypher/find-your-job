package com.fij.services;

import com.fij.models.Chat;
import com.fij.models.Message;
import com.fij.models.User;
import com.fij.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebSocketChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void handleSendMessage(Long chatId, String content) {
        User sender = getCurrentUser();
        
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        
        message = messageRepository.save(message);
        
        chat.setLastMessageAt(java.time.LocalDateTime.now());
        chatRepository.save(chat);
        
        MessageResponse response = new MessageResponse(
                message.getId(),
                chatId,
                sender.getEmail(),
                sender.getFirstName(),
                content,
                null,
                null,
                message.getSentAt(),
                false
        );
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, response);
    }

    public record MessageResponse(
            Long id,
            Long chatId,
            String senderEmail,
            String senderFirstName,
            String content,
            String attachmentUrl,
            String attachmentName,
            java.time.LocalDateTime sentAt,
            boolean read
    ) {}
}
