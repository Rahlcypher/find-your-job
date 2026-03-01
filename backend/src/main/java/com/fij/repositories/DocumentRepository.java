package com.fij.repositories;

import com.fij.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByUserId(Long userId);
}
