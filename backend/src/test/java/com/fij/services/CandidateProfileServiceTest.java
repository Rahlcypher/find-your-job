package com.fij.services;

import com.fij.dto.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private JobPreferenceRepository jobPreferenceRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setRoles(Set.of(Role.ROLE_CANDIDATE));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("test@test.com", null, List.of())
        );
    }

    @Test
    void getExperiences_ReturnsList() {
        Experience exp = new Experience();
        exp.setTitle("Developer");
        exp.setCompany("TechCorp");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(experienceRepository.findByUserId(1L)).thenReturn(List.of(exp));

        List<Experience> result = candidateProfileService.getExperiences();

        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getTitle());
    }

    @Test
    void addExperience_Success_CreatesExperience() {
        ExperienceRequest request = new ExperienceRequest(
            "Developer", "TechCorp", "Description",
            LocalDate.of(2020, 1, 1), LocalDate.of(2023, 12, 31), false
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(experienceRepository.save(any(Experience.class))).thenAnswer(i -> {
            Experience e = i.getArgument(0);
            e.setId(1L);
            return e;
        });

        Experience result = candidateProfileService.addExperience(request);

        assertEquals("Developer", result.getTitle());
        assertEquals("TechCorp", result.getCompany());
    }

    @Test
    void deleteExperience_CallsRepository() {
        candidateProfileService.deleteExperience(1L);

        verify(experienceRepository).deleteById(1L);
    }

    @Test
    void getEducations_ReturnsList() {
        Education edu = new Education();
        edu.setDegree("Bachelor");
        edu.setSchool("University");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(educationRepository.findByUserId(1L)).thenReturn(List.of(edu));

        List<Education> result = candidateProfileService.getEducations();

        assertEquals(1, result.size());
        assertEquals("Bachelor", result.get(0).getDegree());
    }

    @Test
    void addEducation_Success_CreatesEducation() {
        EducationRequest request = new EducationRequest(
            "Bachelor", "University", "Computer Science",
            LocalDate.of(2016, 9, 1), LocalDate.of(2020, 6, 30)
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(educationRepository.save(any(Education.class))).thenAnswer(i -> {
            Education e = i.getArgument(0);
            e.setId(1L);
            return e;
        });

        Education result = candidateProfileService.addEducation(request);

        assertEquals("Bachelor", result.getDegree());
        assertEquals("University", result.getSchool());
    }

    @Test
    void deleteEducation_CallsRepository() {
        candidateProfileService.deleteEducation(1L);

        verify(educationRepository).deleteById(1L);
    }

    @Test
    void getSkills_ReturnsList() {
        Skill skill = new Skill();
        skill.setName("Java");
        skill.setLevel("Advanced");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skill));

        List<Skill> result = candidateProfileService.getSkills();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
    }

    @Test
    void addSkill_Success_CreatesSkill() {
        SkillRequest request = new SkillRequest("Java", "Advanced");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(skillRepository.save(any(Skill.class))).thenAnswer(i -> {
            Skill s = i.getArgument(0);
            s.setId(1L);
            return s;
        });

        Skill result = candidateProfileService.addSkill(request);

        assertEquals("Java", result.getName());
        assertEquals("Advanced", result.getLevel());
    }

    @Test
    void deleteSkill_CallsRepository() {
        candidateProfileService.deleteSkill(1L);

        verify(skillRepository).deleteById(1L);
    }

    @Test
    void getLanguages_ReturnsList() {
        Language lang = new Language();
        lang.setName("English");
        lang.setLevel("Fluent");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(languageRepository.findByUserId(1L)).thenReturn(List.of(lang));

        List<Language> result = candidateProfileService.getLanguages();

        assertEquals(1, result.size());
        assertEquals("English", result.get(0).getName());
    }

    @Test
    void addLanguage_Success_CreatesLanguage() {
        LanguageRequest request = new LanguageRequest("English", "Fluent");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(languageRepository.save(any(Language.class))).thenAnswer(i -> {
            Language l = i.getArgument(0);
            l.setId(1L);
            return l;
        });

        Language result = candidateProfileService.addLanguage(request);

        assertEquals("English", result.getName());
        assertEquals("Fluent", result.getLevel());
    }

    @Test
    void deleteLanguage_CallsRepository() {
        candidateProfileService.deleteLanguage(1L);

        verify(languageRepository).deleteById(1L);
    }

    @Test
    void getJobPreference_WhenExists_ReturnsPreference() {
        JobPreference pref = new JobPreference();
        pref.setMinSalary(50000);
        pref.setMobilityZone("Paris");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(jobPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(pref));

        JobPreference result = candidateProfileService.getJobPreference();

        assertNotNull(result);
        assertEquals(50000, result.getMinSalary());
    }

    @Test
    void getJobPreference_WhenNotExists_ReturnsNull() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(jobPreferenceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        JobPreference result = candidateProfileService.getJobPreference();

        assertNull(result);
    }

    @Test
    void saveJobPreference_Success_CreatesOrUpdates() {
        JobPreferenceRequest request = new JobPreferenceRequest(
            60000, "Lyon", "CDI", "Full-time", "Hybrid"
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(jobPreferenceRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(jobPreferenceRepository.save(any(JobPreference.class))).thenAnswer(i -> i.getArgument(0));

        JobPreference result = candidateProfileService.saveJobPreference(request);

        assertEquals(60000, result.getMinSalary());
        assertEquals("Lyon", result.getMobilityZone());
    }

    @Test
    void saveJobPreference_UpdateExisting() {
        JobPreference existingPref = new JobPreference();
        existingPref.setMinSalary(40000);

        JobPreferenceRequest request = new JobPreferenceRequest(
            60000, "Paris", "CDI", "Full-time", "Remote"
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(jobPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(existingPref));
        when(jobPreferenceRepository.save(any(JobPreference.class))).thenAnswer(i -> i.getArgument(0));

        JobPreference result = candidateProfileService.saveJobPreference(request);

        assertEquals(60000, result.getMinSalary());
    }

    @Test
    void getCV_WhenExists_ReturnsDocument() {
        Document doc = new Document();
        doc.setFileName("cv.pdf");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(documentRepository.findByUserId(1L)).thenReturn(Optional.of(doc));

        Document result = candidateProfileService.getCV();

        assertNotNull(result);
        assertEquals("cv.pdf", result.getFileName());
    }

    @Test
    void getCV_WhenNotExists_ReturnsNull() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(documentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Document result = candidateProfileService.getCV();

        assertNull(result);
    }
}
