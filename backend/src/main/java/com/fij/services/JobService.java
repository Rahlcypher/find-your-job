package com.fij.services;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Job> searchJobs(String location, String jobType, Integer maxDuration) {
        List<Job> jobs = jobRepository.findByActiveTrue();
        
        return jobs.stream()
                .filter(j -> location == null || j.getLocation() != null && j.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(j -> jobType == null || j.getJobType() != null && j.getJobType().equalsIgnoreCase(jobType))
                .filter(j -> maxDuration == null || j.getDuration() == null || j.getDuration() <= maxDuration)
                .toList();
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Transactional
    public Application apply(Long jobId, String coverLetter) {
        User candidate = getCurrentUser();
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.isActive()) {
            throw new RuntimeException("Job is not active");
        }
        
        if (applicationRepository.findByJobIdAndCandidateId(jobId, candidate.getId()).isPresent()) {
            throw new RuntimeException("Already applied to this job");
        }
        
        Application app = new Application();
        app.setJob(job);
        app.setCandidate(candidate);
        app.setCoverLetter(coverLetter);
        app.setStatus("PENDING");
        
        return applicationRepository.save(app);
    }

    public List<ApplicationResponse> getMyApplications() {
        List<Application> applications = applicationRepository.findByCandidateId(getCurrentUser().getId());
        return applications.stream()
                .map(app -> new ApplicationResponse(
                        app.getId(),
                        app.getJob().getId(),
                        app.getJob().getTitle(),
                        app.getJob().getCompany(),
                        app.getJob().getLocation(),
                        app.getCandidate().getId(),
                        app.getCandidate().getFirstName() + " " + app.getCandidate().getLastName(),
                        app.getStatus(),
                        app.getCoverLetter(),
                        app.getAppliedAt()
                ))
                .toList();
    }

    public Application withdrawApplication(Long applicationId) {
        User candidate = getCurrentUser();
        
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!app.getCandidate().getId().equals(candidate.getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        applicationRepository.delete(app);
        return app;
    }
}
