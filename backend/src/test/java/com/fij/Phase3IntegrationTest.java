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
@Transactional
class Phase3IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() throws Exception {
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_RECRUITER"]}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123","firstName":"Jane","lastName":"Smith","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().isOk());
    }

    @Test
    void fullFlow_CreateJob_SearchJob_Apply() throws Exception {
        String recruiterToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String candidateToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String recruiterJwt = extractToken(recruiterToken);
        String candidateJwt = extractToken(candidateToken);

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Java Developer","description":"Backend developer","company":"TechCorp","location":"Paris","salaryMin":50000,"salaryMax":80000,"jobType":"CDI","workSchedule":"Full-time","remotePolicy":"Hybrid","duration":12}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.company").value("TechCorp"));

        mockMvc.perform(get("/api/jobs")
                .param("location", "Paris")
                .param("jobType", "CDI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Developer"));

        mockMvc.perform(post("/api/jobs/1/apply")
                .header("Authorization", "Bearer " + candidateJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"coverLetter":"I am interested in this position"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/jobs/my-applications")
                .header("Authorization", "Bearer " + candidateJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].job.title").value("Java Developer"));

        mockMvc.perform(get("/api/recruiter/jobs/1/applications")
                .header("Authorization", "Bearer " + recruiterJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].candidate.email").value("candidate@test.com"));
    }

    @Test
    void searchJobs_Filters_Works() throws Exception {
        String recruiterToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String recruiterJwt = extractToken(recruiterToken);

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Paris CDI","description":"Desc","company":"C1","location":"Paris","jobType":"CDI","duration":12}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Lyon CDD","description":"Desc","company":"C2","location":"Lyon","jobType":"CDD","duration":6}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/jobs")
                .param("location", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].location").value("Paris"));

        mockMvc.perform(get("/api/jobs")
                .param("maxDuration", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void apply_DuplicateApplication_Rejected() throws Exception {
        String recruiterToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String candidateToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String recruiterJwt = extractToken(recruiterToken);
        String candidateJwt = extractToken(candidateToken);

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Job","description":"Desc","company":"C","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/jobs/1/apply")
                .header("Authorization", "Bearer " + candidateJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverLetter\":\"Letter\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/jobs/1/apply")
                .header("Authorization", "Bearer " + candidateJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverLetter\":\"Letter 2\"}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void updateApplicationStatus() throws Exception {
        String recruiterToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String candidateToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String recruiterJwt = extractToken(recruiterToken);
        String candidateJwt = extractToken(candidateToken);

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Job","description":"Desc","company":"C","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/jobs/1/apply")
                .header("Authorization", "Bearer " + candidateJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverLetter\":\"Letter\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/recruiter/applications/1/status")
                .header("Authorization", "Bearer " + recruiterJwt)
                .param("status", "INTERVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    void withdrawApplication() throws Exception {
        String recruiterToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"recruiter@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String candidateToken = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"candidate@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String recruiterJwt = extractToken(recruiterToken);
        String candidateJwt = extractToken(candidateToken);

        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Job","description":"Desc","company":"C","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/jobs/1/apply")
                .header("Authorization", "Bearer " + candidateJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coverLetter\":\"Letter\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/jobs/applications/1")
                .header("Authorization", "Bearer " + candidateJwt))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/jobs/my-applications")
                .header("Authorization", "Bearer " + candidateJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private String extractToken(String json) {
        int tokenStart = json.indexOf("\"token\":\"") + 9;
        int tokenEnd = json.indexOf("\"", tokenStart);
        return json.substring(tokenStart, tokenEnd);
    }
}
