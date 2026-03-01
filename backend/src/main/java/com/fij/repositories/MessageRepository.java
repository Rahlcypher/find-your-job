package com.fij.repositories;

import com.fij.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);
    long countByChatIdAndReadFalseAndSenderIdNot(Long chatId, Long senderId);
}
