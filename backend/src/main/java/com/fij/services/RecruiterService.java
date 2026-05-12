package com.fij.services;

import com.fij.dto.ApplicationResponse;
import com.fij.dto.JobRequest;
import com.fij.dto.JobResponse;
import com.fij.models.Application;
import com.fij.models.Job;
import com.fij.models.User;
import com.fij.repositories.ApplicationRepository;
import com.fij.repositories.JobRepository;
import com.fij.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<JobResponse> getMyJobs() {
        User recruiter = getCurrentUser();
        return jobRepository.findByRecruiterId(recruiter.getId()).stream()
                .map(this::toJobResponse)
                .toList();
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        User recruiter = getCurrentUser();
        
        Job job = new Job();
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setJobType(request.jobType());
        job.setWorkSchedule(request.workSchedule());
        job.setRemotePolicy(request.remotePolicy());
        job.setDuration(request.duration());
        job.setActive(true);
        job.setCreatedAt(LocalDateTime.now());
        job.setRecruiter(recruiter);
        
        Job saved = jobRepository.save(job);
        return toJobResponse(saved);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        User recruiter = getCurrentUser();
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setCompany(request.company());
        job.setLocation(request.location());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setJobType(request.jobType());
        job.setWorkSchedule(request.workSchedule());
        job.setRemotePolicy(request.remotePolicy());
        job.setDuration(request.duration());
        
        Job saved = jobRepository.save(job);
        return toJobResponse(saved);
    }

    @Transactional
    public void deleteJob(Long id) {
        User recruiter = getCurrentUser();
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        jobRepository.delete(job);
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId) {
        User recruiter = getCurrentUser();
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        return applicationRepository.findByJobId(jobId).stream()
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

    public List<ApplicationResponse> getAllMyApplications() {
        User recruiter = getCurrentUser();
        List<Job> myJobs = jobRepository.findByRecruiterId(recruiter.getId());
        
        return myJobs.stream()
                .flatMap(job -> applicationRepository.findByJobId(job.getId()).stream())
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

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, String status) {
        User recruiter = getCurrentUser();
        
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!app.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        if (!List.of("PENDING", "ACCEPTED", "REJECTED").contains(status)) {
            throw new RuntimeException("Invalid status");
        }
        
        app.setStatus(status);
        Application saved = applicationRepository.save(app);
        
        return new ApplicationResponse(
                saved.getId(),
                saved.getJob().getId(),
                saved.getJob().getTitle(),
                saved.getJob().getCompany(),
                saved.getJob().getLocation(),
                saved.getCandidate().getId(),
                saved.getCandidate().getFirstName() + " " + saved.getCandidate().getLastName(),
                saved.getStatus(),
                saved.getCoverLetter(),
                saved.getAppliedAt()
        );
    }

    private JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompany(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getJobType(),
                job.getWorkSchedule(),
                job.getRemotePolicy(),
                job.getDuration(),
                job.isActive(),
                job.getCreatedAt(),
                job.getExpiresAt(),
                job.getRecruiter().getId(),
                job.getRecruiter().getFirstName() + " " + job.getRecruiter().getLastName()
        );
    }
}
