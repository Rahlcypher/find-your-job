package com.fij.services;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final JobRepository jobRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<ChatResponse> getMyChats() {
        User user = getCurrentUser();
        
        List<Chat> chats;
        if (user.getRoles().contains(Role.ROLE_RECRUITER)) {
            chats = chatRepository.findByRecruiterIdOrderByLastMessageAtDesc(user.getId());
        } else {
            chats = chatRepository.findByCandidateIdOrderByLastMessageAtDesc(user.getId());
        }
        
        return chats.stream().map(chat -> mapToChatResponse(chat, user)).toList();
    }

    @Transactional
    public ChatResponse getOrCreateChat(Long jobId) {
        User user = getCurrentUser();
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Ensure recruiter is loaded
        if (job.getRecruiter() == null) {
            throw new RuntimeException("Job has no recruiter");
        }
        
        Chat chat;
        if (user.getRoles().contains(Role.ROLE_RECRUITER)) {
            // Recruiter can only join an existing chat
            chat = chatRepository.findByJobId(jobId)
                    .orElseThrow(() -> new RuntimeException("No chat found for this job. Candidate must initiate."));
        } else {
            // Candidate creates or gets chat
            chat = chatRepository.findByJobIdAndCandidateId(jobId, user.getId())
                    .orElseGet(() -> createChat(job, job.getRecruiter(), user));
        }
        
        return mapToChatResponse(chat, user);
    }

    private User getRecruiterForJob(Job job) {
        return job.getRecruiter();
    }

    private Chat createChat(Job job, User recruiter, User candidate) {
        Chat chat = new Chat();
        chat.setJob(job);
        chat.setRecruiter(recruiter);
        chat.setCandidate(candidate);
        return chatRepository.save(chat);
    }

    public List<MessageResponse> getMessages(Long chatId) {
        User user = getCurrentUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        if (!isUserInChat(chat, user)) {
            throw new RuntimeException("Not authorized");
        }
        
        return messageRepository.findByChatIdOrderBySentAtAsc(chatId)
                .stream()
                .map(this::mapToMessageResponse)
                .toList();
    }

    @Transactional
    public MessageResponse sendMessage(Long chatId, MessageRequest request) {
        User user = getCurrentUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        if (!isUserInChat(chat, user)) {
            throw new RuntimeException("Not authorized");
        }
        
        Message message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setContent(request.content());
        message.setAttachmentUrl(request.attachmentUrl());
        message.setAttachmentName(request.attachmentName());
        
        message = messageRepository.save(message);
        
        chat.setLastMessageAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        return mapToMessageResponse(message);
    }

    @Transactional
    public void markAsRead(Long chatId) {
        User user = getCurrentUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        
        if (!isUserInChat(chat, user)) {
            throw new RuntimeException("Not authorized");
        }
        
        List<Message> messages = messageRepository.findByChatIdOrderBySentAtAsc(chatId);
        for (Message msg : messages) {
            if (!msg.getSender().getId().equals(user.getId()) && !msg.isRead()) {
                msg.setRead(true);
                messageRepository.save(msg);
            }
        }
    }

    private boolean isUserInChat(Chat chat, User user) {
        return (chat.getCandidate() != null && chat.getCandidate().getId().equals(user.getId())) ||
               (chat.getRecruiter() != null && chat.getRecruiter().getId().equals(user.getId()));
    }

    private ChatResponse mapToChatResponse(Chat chat, User currentUser) {
        User otherUser = chat.getCandidate().getId().equals(currentUser.getId()) 
                ? chat.getRecruiter() 
                : chat.getCandidate();
        
        String lastMessage = "";
        int unreadCount = (int) messageRepository.countByChatIdAndReadFalseAndSenderIdNot(
                chat.getId(), currentUser.getId());
        
        List<Message> messages = messageRepository.findByChatIdOrderBySentAtAsc(chat.getId());
        if (!messages.isEmpty()) {
            lastMessage = messages.get(messages.size() - 1).getContent();
            if (lastMessage.length() > 50) {
                lastMessage = lastMessage.substring(0, 50) + "...";
            }
        }
        
        return new ChatResponse(
                chat.getId(),
                chat.getJob().getId(),
                chat.getJob().getTitle(),
                otherUser.getEmail(),
                otherUser.getFirstName(),
                lastMessage,
                chat.getLastMessageAt(),
                unreadCount
        );
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChat().getId(),
                message.getSender().getEmail(),
                message.getSender().getFirstName(),
                message.getContent(),
                message.getAttachmentUrl(),
                message.getAttachmentName(),
                message.getSentAt(),
                message.isRead()
        );
    }
}
