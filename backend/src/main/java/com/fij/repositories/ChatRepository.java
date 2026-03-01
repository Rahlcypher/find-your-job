package com.fij.repositories;

import com.fij.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByCandidateIdOrderByLastMessageAtDesc(Long candidateId);
    List<Chat> findByRecruiterIdOrderByLastMessageAtDesc(Long recruiterId);
    Optional<Chat> findByJobIdAndCandidateId(Long jobId, Long candidateId);
    Optional<Chat> findByJobIdAndRecruiterId(Long jobId, Long recruiterId);
    Optional<Chat> findByJobId(Long jobId);
}
