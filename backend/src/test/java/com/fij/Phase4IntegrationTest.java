package com.fij;

import com.fij.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class Phase4IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        chatRepository.deleteAll();
        reportRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ====== CHAT TESTS ======

    @Test
    void createChat_AsCandidate_Success() throws Exception {
        String recruiterToken = registerAndLogin("recruiter1@test.com", "ROLE_RECRUITER");
        
        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","description":"Backend dev","company":"TechCorp","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        String candidateToken = registerAndLogin("candidate1@test.com", "ROLE_CANDIDATE");

        mockMvc.perform(post("/api/chats/job/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(1))
                .andExpect(jsonPath("$.jobTitle").value("Developer"));
    }

    @Test
    void getMyChats_ReturnsList() throws Exception {
        String recruiterToken = registerAndLogin("recruiter2@test.com", "ROLE_RECRUITER");
        
        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","description":"Backend dev","company":"TechCorp","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        String candidateToken = registerAndLogin("candidate2@test.com", "ROLE_CANDIDATE");

        mockMvc.perform(post("/api/chats/job/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/chats")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void sendMessage_Success() throws Exception {
        String recruiterToken = registerAndLogin("recruiter3@test.com", "ROLE_RECRUITER");
        
        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","description":"Backend dev","company":"TechCorp","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        String candidateToken = registerAndLogin("candidate3@test.com", "ROLE_CANDIDATE");

        mockMvc.perform(post("/api/chats/job/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chats/1/messages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"content":"Hello, I am interested!"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello, I am interested!"));
    }

    @Test
    void getMessages_ReturnsList() throws Exception {
        String recruiterToken = registerAndLogin("recruiter4@test.com", "ROLE_RECRUITER");
        
        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","description":"Backend dev","company":"TechCorp","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        String candidateToken = registerAndLogin("candidate4@test.com", "ROLE_CANDIDATE");

        mockMvc.perform(post("/api/chats/job/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chats/1/messages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"content":"Hello!"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/chats/1/messages")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void sendMessage_OtherUserCanSee() throws Exception {
        String recruiterToken = registerAndLogin("recruiter5@test.com", "ROLE_RECRUITER");
        
        mockMvc.perform(post("/api/recruiter/jobs")
                .header("Authorization", "Bearer " + recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"Developer","description":"Backend dev","company":"TechCorp","location":"Paris","jobType":"CDI"}
                    """))
                .andExpect(status().isOk());

        String candidateToken = registerAndLogin("candidate5@test.com", "ROLE_CANDIDATE");

        mockMvc.perform(post("/api/chats/job/1")
                .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/chats/1/messages")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"content":"Message from candidate"}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/chats/1/messages")
                .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Message from candidate"));
    }

    // ====== REPORT TESTS ======

    @Test
    void reportUser_Success() throws Exception {
        String reporterToken = registerAndLogin("reporter1@test.com", "ROLE_CANDIDATE");
        
        String reportedToken = registerAndLogin("reported1@test.com", "ROLE_RECRUITER");
        Long reportedId = userRepository.findByEmail("reported1@test.com").get().getId();

        mockMvc.perform(post("/api/reports")
                .header("Authorization", "Bearer " + reporterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"reportedUserId":%d,"reason":"Spam","description":"This is spam"}
                    """, reportedId)))
                .andExpect(status().isOk());
    }

    @Test
    void reportUser_SelfReport_Returns400() throws Exception {
        String candidateToken = registerAndLogin("candidate6@test.com", "ROLE_CANDIDATE");
        Long candidateId = userRepository.findByEmail("candidate6@test.com").get().getId();

        mockMvc.perform(post("/api/reports")
                .header("Authorization", "Bearer " + candidateToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"reportedUserId":%d,"reason":"Spam","description":"This is spam"}
                    """, candidateId)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getMyReports_ReturnsList() throws Exception {
        String reporterToken = registerAndLogin("reporter2@test.com", "ROLE_CANDIDATE");
        
        registerAndLogin("reported2@test.com", "ROLE_RECRUITER");
        Long reportedId = userRepository.findByEmail("reported2@test.com").get().getId();

        mockMvc.perform(post("/api/reports")
                .header("Authorization", "Bearer " + reporterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"reportedUserId":%d,"reason":"Spam","description":"Spam message"}
                    """, reportedId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/my-reports")
                .header("Authorization", "Bearer " + reporterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reason").value("Spam"));
    }

    private String registerAndLogin(String email, String role) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"email":"%s","password":"password123","firstName":"John","lastName":"Doe","roles":["%s"]}
                    """, email, role)))
                .andExpect(status().isOk());

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"email":"%s","password":"password123"}
                    """, email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int tokenStart = response.indexOf("\"token\":\"") + 9;
        int tokenEnd = response.indexOf("\"", tokenStart);
        return response.substring(tokenStart, tokenEnd);
    }
}
