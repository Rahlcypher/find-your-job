package com.fij.services;

import com.fij.dto.ReportRequest;
import com.fij.models.*;
import com.fij.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void reportUser(ReportRequest request) {
        User reporter = getCurrentUser();
        
        User reportedUser = userRepository.findById(request.reportedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (reporter.getId().equals(reportedUser.getId())) {
            throw new RuntimeException("Cannot report yourself");
        }
        
        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReason(request.reason());
        report.setDescription(request.description());
        
        reportRepository.save(report);
    }

    public List<Report> getMyReports() {
        return reportRepository.findByReporterId(getCurrentUser().getId());
    }
}
