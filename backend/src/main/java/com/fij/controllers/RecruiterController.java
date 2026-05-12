package com.fij.controllers;

import com.fij.dto.ApplicationResponse;
import com.fij.dto.JobRequest;
import com.fij.dto.JobResponse;
import com.fij.services.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        return ResponseEntity.ok(recruiterService.getMyJobs());
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest request) {
        return ResponseEntity.ok(recruiterService.createJob(request));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        return ResponseEntity.ok(recruiterService.updateJob(id, request));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        recruiterService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{id}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForJob(@PathVariable Long id) {
        return ResponseEntity.ok(recruiterService.getApplicationsForJob(id));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllMyApplications() {
        return ResponseEntity.ok(recruiterService.getAllMyApplications());
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(recruiterService.updateApplicationStatus(id, request.status()));
    }

    public record StatusUpdateRequest(String status) {}
}
