package com.fij.repositories;

import com.fij.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterId(Long reporterId);
    List<Report> findByReportedUserId(Long reportedUserId);
    List<Report> findByStatus(String status);
}
