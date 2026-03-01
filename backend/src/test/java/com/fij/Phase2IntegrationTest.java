package com.fij;

import com.fij.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class Phase2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private JobPreferenceRepository jobPreferenceRepository;

    private String candidateToken;

    @BeforeEach
    void setUp() throws Exception {
        experienceRepository.deleteAll();
        educationRepository.deleteAll();
        skillRepository.deleteAll();
        languageRepository.deleteAll();
        jobPreferenceRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().isOk());

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        candidateToken = extractToken(response);
    }

    // ====== EXPERIENCES ======

    @Test
    void addExperience_Success_ReturnsExperience() throws Exception {
        mockMvc.perform(post("/api/candidate/experiences")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","company":"TechCorp","description":"Backend dev","startDate":"2020-01-01","endDate":"2023-12-31","currentJob":false}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Developer"))
                .andExpect(jsonPath("$.company").value("TechCorp"));
    }

    @Test
    void getExperiences_ReturnsList() throws Exception {
        mockMvc.perform(post("/api/candidate/experiences")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","company":"TechCorp","description":"Backend dev","startDate":"2020-01-01","endDate":"2023-12-31","currentJob":false}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/candidate/experiences")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Developer"));
    }

    @Test
    void deleteExperience_Success() throws Exception {
        mockMvc.perform(post("/api/candidate/experiences")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","company":"TechCorp","description":"Backend dev","startDate":"2020-01-01","endDate":"2023-12-31","currentJob":false}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/candidate/experiences/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isNoContent());
    }

    // ====== EDUCATION ======

    @Test
    void addEducation_Success_ReturnsEducation() throws Exception {
        mockMvc.perform(post("/api/candidate/educations")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"degree":"Bachelor","school":"MIT","fieldOfStudy":"Computer Science","startDate":"2016-09-01","endDate":"2020-06-30"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degree").value("Bachelor"))
                .andExpect(jsonPath("$.school").value("MIT"));
    }

    @Test
    void getEducations_ReturnsList() throws Exception {
        mockMvc.perform(post("/api/candidate/educations")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"degree":"Bachelor","school":"MIT","fieldOfStudy":"Computer Science","startDate":"2016-09-01","endDate":"2020-06-30"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/candidate/educations")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].degree").value("Bachelor"));
    }

    @Test
    void deleteEducation_Success() throws Exception {
        mockMvc.perform(post("/api/candidate/educations")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"degree":"Bachelor","school":"MIT","fieldOfStudy":"Computer Science","startDate":"2016-09-01","endDate":"2020-06-30"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/candidate/educations/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isNoContent());
    }

    // ====== SKILLS ======

    @Test
    void addSkill_Success_ReturnsSkill() throws Exception {
        mockMvc.perform(post("/api/candidate/skills")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Java","level":"Advanced"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.level").value("Advanced"));
    }

    @Test
    void getSkills_ReturnsList() throws Exception {
        mockMvc.perform(post("/api/candidate/skills")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Java","level":"Advanced"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/candidate/skills")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    void deleteSkill_Success() throws Exception {
        mockMvc.perform(post("/api/candidate/skills")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Java","level":"Advanced"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/candidate/skills/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isNoContent());
    }

    // ====== LANGUAGES ======

    @Test
    void addLanguage_Success_ReturnsLanguage() throws Exception {
        mockMvc.perform(post("/api/candidate/languages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"English","level":"Fluent"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("English"))
                .andExpect(jsonPath("$.level").value("Fluent"));
    }

    @Test
    void getLanguages_ReturnsList() throws Exception {
        mockMvc.perform(post("/api/candidate/languages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"English","level":"Fluent"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/candidate/languages")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("English"));
    }

    @Test
    void deleteLanguage_Success() throws Exception {
        mockMvc.perform(post("/api/candidate/languages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"English","level":"Fluent"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/candidate/languages/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isNoContent());
    }

    // ====== JOB PREFERENCES ======

    @Test
    void saveJobPreference_Success_ReturnsPreference() throws Exception {
        mockMvc.perform(post("/api/candidate/preferences")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"minSalary":60000,"mobilityZone":"Paris","jobType":"CDI","workSchedule":"Full-time","remotePreference":"Hybrid"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minSalary").value(60000))
                .andExpect(jsonPath("$.mobilityZone").value("Paris"))
                .andExpect(jsonPath("$.jobType").value("CDI"));
    }

    @Test
    void getJobPreference_WhenNotExists_ReturnsNull() throws Exception {
        mockMvc.perform(get("/api/candidate/preferences")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getJobPreference_WhenExists_ReturnsPreference() throws Exception {
        mockMvc.perform(post("/api/candidate/preferences")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"minSalary":60000,"mobilityZone":"Paris","jobType":"CDI","workSchedule":"Full-time","remotePreference":"Hybrid"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/candidate/preferences")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minSalary").value(60000));
    }

    private String extractToken(String json) {
        int tokenStart = json.indexOf("\"token\":\"") + 9;
        int tokenEnd = json.indexOf("\"", tokenStart);
        return json.substring(tokenStart, tokenEnd);
    }
}
