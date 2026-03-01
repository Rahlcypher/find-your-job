package com.fij.controllers;

import com.fij.dto.*;
import com.fij.models.*;
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

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@RequestBody JobRequest request) {
        return ResponseEntity.ok(recruiterService.createJob(request));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getMyJobs() {
        return ResponseEntity.ok(recruiterService.getMyJobs());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(recruiterService.getJob(id));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        return ResponseEntity.ok(recruiterService.updateJob(id, request));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        recruiterService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(recruiterService.getApplicationsForJob(jobId));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        return ResponseEntity.ok(recruiterService.updateApplicationStatus(id, status));
    }
}
