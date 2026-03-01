package com.fij.controllers;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Integer maxDuration) {
        return ResponseEntity.ok(jobService.searchJobs(location, jobType, maxDuration));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Application> apply(
            @PathVariable Long id, 
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(jobService.apply(id, request.coverLetter()));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<Application>> getMyApplications() {
        return ResponseEntity.ok(jobService.getMyApplications());
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long id) {
        jobService.withdrawApplication(id);
        return ResponseEntity.noContent().build();
    }
}
