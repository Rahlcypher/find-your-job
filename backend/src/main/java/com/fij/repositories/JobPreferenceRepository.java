package com.fij.repositories;

import com.fij.models.JobPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobPreferenceRepository extends JpaRepository<JobPreference, Long> {
    Optional<JobPreference> findByUserId(Long userId);
}
