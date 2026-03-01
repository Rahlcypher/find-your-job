package com.fij.services;

import com.fij.dto.MessageRequest;
import com.fij.models.*;
import com.fij.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ChatService chatService;

    private User candidate;
    private User recruiter;
    private Job job;
    private Chat chat;

    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(1L);
        candidate.setEmail("candidate@test.com");
        candidate.setFirstName("Jane");
        candidate.setRoles(Set.of(Role.ROLE_CANDIDATE));

        recruiter = new User();
        recruiter.setId(2L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setFirstName("John");
        recruiter.setRoles(Set.of(Role.ROLE_RECRUITER));

        job = new Job();
        job.setId(1L);
        job.setTitle("Developer");
        job.setRecruiter(recruiter);

        chat = new Chat();
        chat.setId(1L);
        chat.setJob(job);
        chat.setCandidate(candidate);
        chat.setRecruiter(recruiter);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("candidate@test.com", null, List.of())
        );
    }

    @Test
    void getMyChats_Candidate_ReturnsChats() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(chatRepository.findByCandidateIdOrderByLastMessageAtDesc(1L)).thenReturn(List.of(chat));
        when(messageRepository.findByChatIdOrderBySentAtAsc(1L)).thenReturn(List.of());
        when(messageRepository.countByChatIdAndReadFalseAndSenderIdNot(any(), any())).thenReturn(0L);

        var result = chatService.getMyChats();

        assertEquals(1, result.size());
    }

    @Test
    void getOrCreateChat_NewChat_CreatesChat() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(chatRepository.findByJobIdAndCandidateId(1L, 1L)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(i -> {
            Chat c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        var result = chatService.getOrCreateChat(1L);

        assertNotNull(result);
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void getOrCreateChat_ExistingChat_ReturnsExisting() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(chatRepository.findByJobIdAndCandidateId(1L, 1L)).thenReturn(Optional.of(chat));
        when(messageRepository.findByChatIdOrderBySentAtAsc(1L)).thenReturn(List.of());
        when(messageRepository.countByChatIdAndReadFalseAndSenderIdNot(any(), any())).thenReturn(0L);

        var result = chatService.getOrCreateChat(1L);

        assertEquals(1L, result.id());
        verify(chatRepository, never()).save(any());
    }

    @Test
    void getMessages_Authorized_ReturnsMessages() {
        Message message = new Message();
        message.setId(1L);
        message.setContent("Hello");
        message.setSender(candidate);
        message.setChat(chat);

        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(messageRepository.findByChatIdOrderBySentAtAsc(1L)).thenReturn(List.of(message));

        var result = chatService.getMessages(1L);

        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).content());
    }

    @Test
    void sendMessage_Success_CreatesMessage() {
        MessageRequest request = new MessageRequest("Hello", null, null);

        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(chatRepository.save(any(Chat.class))).thenAnswer(i -> i.getArgument(0));

        var result = chatService.sendMessage(1L, request);

        assertNotNull(result);
        assertEquals("Hello", result.content());
    }

    @Test
    void markAsRead_Success_MarksMessagesAsRead() {
        Message message = new Message();
        message.setId(1L);
        message.setSender(recruiter);
        message.setRead(false);

        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(messageRepository.findByChatIdOrderBySentAtAsc(1L)).thenReturn(List.of(message));

        chatService.markAsRead(1L);

        assertTrue(message.isRead());
        verify(messageRepository).save(message);
    }
}
