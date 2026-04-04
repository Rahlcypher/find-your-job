package com.fij.controllers;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.services.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateProfileService candidateProfileService;

    @GetMapping("/experiences")
    public ResponseEntity<List<Experience>> getExperiences() {
        return ResponseEntity.ok(candidateProfileService.getExperiences());
    }

    @PostMapping("/experiences")
    public ResponseEntity<Experience> addExperience(@RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(candidateProfileService.addExperience(request));
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        candidateProfileService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/experiences/{id}")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long id, @RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateExperience(id, request));
    }

    @GetMapping("/educations")
    public ResponseEntity<List<Education>> getEducations() {
        return ResponseEntity.ok(candidateProfileService.getEducations());
    }

    @PostMapping("/educations")
    public ResponseEntity<Education> addEducation(@RequestBody EducationRequest request) {
        return ResponseEntity.ok(candidateProfileService.addEducation(request));
    }

    @DeleteMapping("/educations/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        candidateProfileService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/educations/{id}")
    public ResponseEntity<Education> updateEducation(@PathVariable Long id, @RequestBody EducationRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateEducation(id, request));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<Skill>> getSkills() {
        return ResponseEntity.ok(candidateProfileService.getSkills());
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> addSkill(@RequestBody SkillRequest request) {
        return ResponseEntity.ok(candidateProfileService.addSkill(request));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        candidateProfileService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody SkillRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateSkill(id, request));
    }

    @GetMapping("/languages")
    public ResponseEntity<List<Language>> getLanguages() {
        return ResponseEntity.ok(candidateProfileService.getLanguages());
    }

    @PostMapping("/languages")
    public ResponseEntity<Language> addLanguage(@RequestBody LanguageRequest request) {
        return ResponseEntity.ok(candidateProfileService.addLanguage(request));
    }

    @DeleteMapping("/languages/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        candidateProfileService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/languages/{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable Long id, @RequestBody LanguageRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateLanguage(id, request));
    }

    @GetMapping("/preferences")
    public ResponseEntity<JobPreference> getJobPreference() {
        return ResponseEntity.ok(candidateProfileService.getJobPreference());
    }

    @PostMapping("/preferences")
    public ResponseEntity<JobPreference> saveJobPreference(@RequestBody JobPreferenceRequest request) {
        return ResponseEntity.ok(candidateProfileService.saveJobPreference(request));
    }

    @GetMapping("/cv")
    public ResponseEntity<Document> getCV() {
        Document cv = candidateProfileService.getCV();
        if (cv == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cv);
    }

    @PostMapping("/cv")
    public ResponseEntity<Document> uploadCV(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(candidateProfileService.uploadCV(file));
    }

    @DeleteMapping("/cv")
    public ResponseEntity<Void> deleteCV() {
        candidateProfileService.deleteCV();
        return ResponseEntity.noContent().build();
    }
}
