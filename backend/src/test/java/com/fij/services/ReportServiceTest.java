package com.fij.services;

import com.fij.dto.ReportRequest;
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
class ReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private User reporter;
    private User reportedUser;

    @BeforeEach
    void setUp() {
        reporter = new User();
        reporter.setId(1L);
        reporter.setEmail("reporter@test.com");
        reporter.setRoles(Set.of(Role.ROLE_CANDIDATE));

        reportedUser = new User();
        reportedUser.setId(2L);
        reportedUser.setEmail("reported@test.com");
        reportedUser.setRoles(Set.of(Role.ROLE_RECRUITER));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("reporter@test.com", null, List.of())
        );
    }

    @Test
    void reportUser_Success_CreatesReport() {
        ReportRequest request = new ReportRequest(2L, "Spam", "This is spam");

        when(userRepository.findByEmail("reporter@test.com")).thenReturn(Optional.of(reporter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reportedUser));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> reportService.reportUser(request));

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void reportUser_SelfReport_ThrowsException() {
        ReportRequest request = new ReportRequest(1L, "Spam", "This is spam");

        when(userRepository.findByEmail("reporter@test.com")).thenReturn(Optional.of(reporter));

        assertThrows(RuntimeException.class, () -> reportService.reportUser(request));
    }

    @Test
    void reportUser_InvalidUser_ThrowsException() {
        ReportRequest request = new ReportRequest(999L, "Spam", "This is spam");

        when(userRepository.findByEmail("reporter@test.com")).thenReturn(Optional.of(reporter));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.reportUser(request));
    }

    @Test
    void getMyReports_ReturnsReports() {
        Report report = new Report();
        report.setReason("Spam");
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);

        when(userRepository.findByEmail("reporter@test.com")).thenReturn(Optional.of(reporter));
        when(reportRepository.findByReporterId(1L)).thenReturn(List.of(report));

        var result = reportService.getMyReports();

        assertEquals(1, result.size());
        assertEquals("Spam", result.get(0).getReason());
    }
}
