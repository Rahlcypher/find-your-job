package com.fij.services;

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
class JobServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private JobService jobService;

    private User candidate;
    private User recruiter;
    private Job job;

    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(1L);
        candidate.setEmail("candidate@test.com");
        candidate.setRoles(Set.of(Role.ROLE_CANDIDATE));

        recruiter = new User();
        recruiter.setId(2L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRoles(Set.of(Role.ROLE_RECRUITER));

        job = new Job();
        job.setId(1L);
        job.setTitle("Developer");
        job.setLocation("Paris");
        job.setJobType("CDI");
        job.setDuration(12);
        job.setActive(true);
        job.setRecruiter(recruiter);
    }

    private void setSecurityContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );
    }

    @Test
    void searchJobs_WithFilters_ReturnsFilteredJobs() {
        when(jobRepository.findByActiveTrue()).thenReturn(List.of(job));
        
        List<Job> result = jobService.searchJobs("Paris", "CDI", 24);
        
        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getTitle());
    }

    @Test
    void searchJobs_WithLocationFilter_MatchesPartial() {
        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Designer");
        job2.setLocation("Lyon");
        job2.setJobType("CDI");
        job2.setActive(true);
        
        when(jobRepository.findByActiveTrue()).thenReturn(List.of(job, job2));
        
        List<Job> result = jobService.searchJobs("Pa", null, null);
        
        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getTitle());
    }

    @Test
    void searchJobs_WithDurationFilter_FiltersCorrectly() {
        Job longJob = new Job();
        longJob.setId(2L);
        longJob.setTitle("Long term");
        longJob.setDuration(36);
        longJob.setActive(true);
        
        when(jobRepository.findByActiveTrue()).thenReturn(List.of(job, longJob));
        
        List<Job> result = jobService.searchJobs(null, null, 24);
        
        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getTitle());
    }

    @Test
    void getJob_WhenExists_ReturnsJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        Job result = jobService.getJob(1L);
        
        assertEquals("Developer", result.getTitle());
    }

    @Test
    void getJob_WhenNotExists_ThrowsException() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> jobService.getJob(99L));
    }

    @Test
    void apply_Success_CreatesApplication() {
        setSecurityContext(candidate);
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobIdAndCandidateId(1L, 1L)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
        
        Application result = jobService.apply(1L, "Cover letter");
        
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals("Cover letter", result.getCoverLetter());
    }

    @Test
    void apply_AlreadyApplied_ThrowsException() {
        setSecurityContext(candidate);
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobIdAndCandidateId(1L, 1L)).thenReturn(Optional.of(new Application()));
        
        assertThrows(RuntimeException.class, () -> jobService.apply(1L, "Cover letter"));
    }

    @Test
    void apply_InactiveJob_ThrowsException() {
        setSecurityContext(candidate);
        job.setActive(false);
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        assertThrows(RuntimeException.class, () -> jobService.apply(1L, "Cover letter"));
    }

    @Test
    void getMyApplications_ReturnsCandidateApplications() {
        setSecurityContext(candidate);
        Application app = new Application();
        app.setCandidate(candidate);
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(applicationRepository.findByCandidateId(1L)).thenReturn(List.of(app));
        
        List<Application> result = jobService.getMyApplications();
        
        assertEquals(1, result.size());
    }

    @Test
    void withdrawApplication_Success_DeletesApplication() {
        setSecurityContext(candidate);
        Application app = new Application();
        app.setId(1L);
        app.setCandidate(candidate);
        
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(candidate));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        
        jobService.withdrawApplication(1L);
        
        verify(applicationRepository).delete(app);
    }
}
