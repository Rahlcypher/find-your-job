package com.fij.services;

import com.fij.dto.*;
import com.fij.models.*;
import com.fij.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final LanguageRepository languageRepository;
    private final JobPreferenceRepository jobPreferenceRepository;
    private final DocumentRepository documentRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Experience> getExperiences() {
        return experienceRepository.findByUserId(getCurrentUser().getId());
    }

    public Experience addExperience(ExperienceRequest request) {
        Experience exp = new Experience();
        exp.setTitle(request.title());
        exp.setCompany(request.company());
        exp.setDescription(request.description());
        exp.setStartDate(request.startDate());
        exp.setEndDate(request.endDate());
        exp.setCurrentJob(request.currentJob());
        exp.setUser(getCurrentUser());
        return experienceRepository.save(exp);
    }

    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    public List<Education> getEducations() {
        return educationRepository.findByUserId(getCurrentUser().getId());
    }

    public Education addEducation(EducationRequest request) {
        Education edu = new Education();
        edu.setDegree(request.degree());
        edu.setSchool(request.school());
        edu.setFieldOfStudy(request.fieldOfStudy());
        edu.setStartDate(request.startDate());
        edu.setEndDate(request.endDate());
        edu.setUser(getCurrentUser());
        return educationRepository.save(edu);
    }

    public void deleteEducation(Long id) {
        educationRepository.deleteById(id);
    }

    public List<Skill> getSkills() {
        return skillRepository.findByUserId(getCurrentUser().getId());
    }

    public Skill addSkill(SkillRequest request) {
        Skill skill = new Skill();
        skill.setName(request.name());
        skill.setLevel(request.level());
        skill.setUser(getCurrentUser());
        return skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public List<Language> getLanguages() {
        return languageRepository.findByUserId(getCurrentUser().getId());
    }

    public Language addLanguage(LanguageRequest request) {
        Language lang = new Language();
        lang.setName(request.name());
        lang.setLevel(request.level());
        lang.setUser(getCurrentUser());
        return languageRepository.save(lang);
    }

    public void deleteLanguage(Long id) {
        languageRepository.deleteById(id);
    }

    public JobPreference getJobPreference() {
        return jobPreferenceRepository.findByUserId(getCurrentUser().getId()).orElse(null);
    }

    public JobPreference saveJobPreference(JobPreferenceRequest request) {
        User user = getCurrentUser();
        JobPreference pref = jobPreferenceRepository.findByUserId(user.getId())
                .orElse(new JobPreference());
        pref.setMinSalary(request.minSalary());
        pref.setMobilityZone(request.mobilityZone());
        pref.setJobType(request.jobType());
        pref.setWorkSchedule(request.workSchedule());
        pref.setRemotePreference(request.remotePreference());
        pref.setUser(user);
        return jobPreferenceRepository.save(pref);
    }

    public Document uploadCV(MultipartFile file) throws IOException {
        User user = getCurrentUser();
        
        String uploadDir = "uploads/cv/" + user.getId();
        Path dirPath = Paths.get(uploadDir);
        Files.createDirectories(dirPath);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = dirPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        documentRepository.findByUserId(user.getId()).ifPresent(doc -> {
            try {
                Files.deleteIfExists(Paths.get(doc.getFilePath()));
            } catch (IOException e) {}
            documentRepository.delete(doc);
        });

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFilePath(filePath.toString());
        doc.setFileSize(file.getSize());
        doc.setUploadedAt(LocalDateTime.now());
        doc.setUser(user);
        return documentRepository.save(doc);
    }

    public Document getCV() {
        return documentRepository.findByUserId(getCurrentUser().getId()).orElse(null);
    }

    public void deleteCV() {
        documentRepository.findByUserId(getCurrentUser().getId()).ifPresent(doc -> {
            try {
                Files.deleteIfExists(Paths.get(doc.getFilePath()));
            } catch (IOException e) {}
            documentRepository.delete(doc);
        });
    }
}
