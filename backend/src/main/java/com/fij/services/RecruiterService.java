package com.fij.services;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.repositories.*;
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

    public Job createJob(JobRequest request) {
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
        
        return jobRepository.save(job);
    }

    public List<Job> getMyJobs() {
        return jobRepository.findByRecruiterId(getCurrentUser().getId());
    }

    public Job getJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getRecruiter().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized");
        }
        return job;
    }

    public Job updateJob(Long id, JobRequest request) {
        Job job = getJob(id);
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
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = getJob(id);
        job.setActive(false);
        jobRepository.save(job);
    }

    public List<Application> getApplicationsForJob(Long jobId) {
        getJob(jobId);
        return applicationRepository.findByJobId(jobId);
    }

    public Application updateApplicationStatus(Long applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!app.getJob().getRecruiter().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Not authorized");
        }
        
        app.setStatus(status);
        return applicationRepository.save(app);
    }
}
