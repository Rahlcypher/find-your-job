package com.fij.services;

import com.fij.dto.ApplicationResponse;
import com.fij.dto.JobRequest;
import com.fij.dto.JobResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private RecruiterService recruiterService;

    private User testRecruiter;
    private User testCandidate;
    private Job testJob;

    @BeforeEach
    void setUp() {
        testRecruiter = new User();
        testRecruiter.setId(1L);
        testRecruiter.setEmail("recruiter@test.com");
        testRecruiter.setFirstName("John");
        testRecruiter.setLastName("Recruiter");
        testRecruiter.setRoles(Set.of(Role.ROLE_RECRUITER));

        testCandidate = new User();
        testCandidate.setId(2L);
        testCandidate.setEmail("candidate@test.com");
        testCandidate.setFirstName("Jane");
        testCandidate.setLastName("Candidate");
        testCandidate.setRoles(Set.of(Role.ROLE_CANDIDATE));

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Developer");
        testJob.setCompany("TechCorp");
        testJob.setLocation("Paris");
        testJob.setActive(true);
        testJob.setRecruiter(testRecruiter);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("recruiter@test.com", null, List.of())
        );
    }

    @Test
    void getMyJobs_ReturnsJobsForRecruiter() {
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.findByRecruiterId(1L)).thenReturn(List.of(testJob));

        List<JobResponse> result = recruiterService.getMyJobs();

        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).title());
    }

    @Test
    void createJob_Success_CreatesJob() {
        JobRequest request = new JobRequest(
            "Senior Developer", "Full-stack role", "TechCorp", "Lyon",
            50000, 70000, "CDI", "Full-time", "Hybrid", 12
        );

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> {
            Job j = i.getArgument(0);
            j.setId(1L);
            return j;
        });

        JobResponse result = recruiterService.createJob(request);

        assertEquals("Senior Developer", result.title());
        assertTrue(result.active());
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void updateJob_Success_UpdatesJob() {
        JobRequest request = new JobRequest(
            "Lead Developer", "Updated description", "TechCorp", "Paris",
            60000, 80000, "CDI", "Full-time", "Remote", 24
        );

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));

        JobResponse result = recruiterService.updateJob(1L, request);

        assertEquals("Lead Developer", result.title());
        assertEquals("Remote", result.remotePolicy());
    }

    @Test
    void updateJob_NotAuthorized_ThrowsException() {
        User otherRecruiter = new User();
        otherRecruiter.setId(99L);
        
        Job otherJob = new Job();
        otherJob.setId(2L);
        otherJob.setRecruiter(otherRecruiter);

        JobRequest request = new JobRequest(
            "Developer", "Desc", "Company", "Paris",
            null, null, "CDI", null, null, null
        );

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.findById(2L)).thenReturn(Optional.of(otherJob));

        assertThrows(RuntimeException.class, () -> recruiterService.updateJob(2L, request));
    }

    @Test
    void deleteJob_Success_DeletesJob() {
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        recruiterService.deleteJob(1L);

        verify(jobRepository).delete(testJob);
    }

    @Test
    void getApplicationsForJob_ReturnsApplications() {
        Application app = new Application();
        app.setId(1L);
        app.setJob(testJob);
        app.setCandidate(testCandidate);
        app.setStatus("PENDING");
        app.setAppliedAt(LocalDateTime.now());

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        when(applicationRepository.findByJobId(1L)).thenReturn(List.of(app));

        List<ApplicationResponse> result = recruiterService.getApplicationsForJob(1L);

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).status());
    }

    @Test
    void updateApplicationStatus_Success_UpdatesStatus() {
        Application app = new Application();
        app.setId(1L);
        app.setJob(testJob);
        app.setCandidate(testCandidate);
        app.setStatus("PENDING");
        app.setAppliedAt(LocalDateTime.now());

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        ApplicationResponse result = recruiterService.updateApplicationStatus(1L, "ACCEPTED");

        assertEquals("ACCEPTED", result.status());
    }

    @Test
    void updateApplicationStatus_InvalidStatus_ThrowsException() {
        Application app = new Application();
        app.setId(1L);
        app.setJob(testJob);
        app.setCandidate(testCandidate);

        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(testRecruiter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(RuntimeException.class, () -> recruiterService.updateApplicationStatus(1L, "INVALID"));
    }
}
