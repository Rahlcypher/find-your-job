package com.fij.services;

import com.fij.dto.JobRequest;
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
class RecruiterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private RecruiterService recruiterService;

    private User recruiter;
    private Job job;

    @BeforeEach
    void setUp() {
        recruiter = new User();
        recruiter.setId(1L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRoles(Set.of(Role.ROLE_RECRUITER));

        job = new Job();
        job.setId(1L);
        job.setTitle("Developer");
        job.setLocation("Paris");
        job.setJobType("CDI");
        job.setActive(true);
        job.setRecruiter(recruiter);
    }

    private void setSecurityContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );
    }

    @Test
    void createJob_Success_CreatesJob() {
        setSecurityContext(recruiter);
        JobRequest request = new JobRequest(
            "Developer", "Description", "Company", 
            "Paris", 40000, 60000, "CDI", "Full-time", "Remote", 12
        );
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> {
            Job j = i.getArgument(0);
            j.setId(1L);
            return j;
        });
        
        Job result = recruiterService.createJob(request);
        
        assertNotNull(result);
        assertEquals("Developer", result.getTitle());
        assertEquals("Company", result.getCompany());
        assertTrue(result.isActive());
    }

    @Test
    void getMyJobs_ReturnsRecruiterJobs() {
        setSecurityContext(recruiter);
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findByRecruiterId(1L)).thenReturn(List.of(job));
        
        List<Job> result = recruiterService.getMyJobs();
        
        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getTitle());
    }

    @Test
    void getJob_Success_ReturnsJob() {
        setSecurityContext(recruiter);
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        Job result = recruiterService.getJob(1L);
        
        assertEquals("Developer", result.getTitle());
    }

    @Test
    void getJob_NotOwner_ThrowsException() {
        User otherRecruiter = new User();
        otherRecruiter.setId(2L);
        otherRecruiter.setEmail("other@test.com");
        
        setSecurityContext(otherRecruiter);
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherRecruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        assertThrows(RuntimeException.class, () -> recruiterService.getJob(1L));
    }

    @Test
    void updateJob_Success_UpdatesJob() {
        setSecurityContext(recruiter);
        JobRequest request = new JobRequest(
            "Senior Developer", "New Description", "Company",
            "Lyon", 50000, 80000, "CDI", "Full-time", "On-site", 24
        );
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));
        
        Job result = recruiterService.updateJob(1L, request);
        
        assertEquals("Senior Developer", result.getTitle());
        assertEquals("Lyon", result.getLocation());
    }

    @Test
    void deleteJob_Success_DeactivatesJob() {
        setSecurityContext(recruiter);
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));
        
        recruiterService.deleteJob(1L);
        
        assertFalse(job.isActive());
        verify(jobRepository).save(job);
    }

    @Test
    void getApplicationsForJob_ReturnsApplications() {
        setSecurityContext(recruiter);
        Application app = new Application();
        app.setJob(job);
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobId(1L)).thenReturn(List.of(app));
        
        List<Application> result = recruiterService.getApplicationsForJob(1L);
        
        assertEquals(1, result.size());
    }

    @Test
    void updateApplicationStatus_Success_UpdatesStatus() {
        setSecurityContext(recruiter);
        Application app = new Application();
        app.setJob(job);
        app.setStatus("PENDING");
        when(userRepository.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));
        
        Application result = recruiterService.updateApplicationStatus(1L, "INTERVIEW");
        
        assertEquals("INTERVIEW", result.getStatus());
    }

    @Test
    void updateApplicationStatus_NotOwner_ThrowsException() {
        User otherRecruiter = new User();
        otherRecruiter.setId(2L);
        
        Application app = new Application();
        app.setJob(job);
        
        setSecurityContext(otherRecruiter);
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherRecruiter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        
        assertThrows(RuntimeException.class, () -> recruiterService.updateApplicationStatus(1L, "REJECTED"));
    }
}
