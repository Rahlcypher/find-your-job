package com.fij.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fij.config.JwtAuthenticationFilter;
import com.fij.config.SecurityConfig;
import com.fij.dto.ApplicationResponse;
import com.fij.dto.JobRequest;
import com.fij.dto.JobResponse;
import com.fij.services.RecruiterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecruiterController.class)
@Import(SecurityConfig.class)
class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecruiterService recruiterService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "RECRUITER")
    void getMyJobs_ReturnsListOfJobs() throws Exception {
        JobResponse job = new JobResponse(
            1L, "Developer", "Desc", "TechCorp", "Paris",
            50000, 70000, "CDI", "Full-time", "Hybrid", 12,
            true, LocalDateTime.now(), null, 1L, "John Recruiter"
        );
        
        when(recruiterService.getMyJobs()).thenReturn(List.of(job));

        mockMvc.perform(get("/api/recruiter/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Developer"));
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void getMyJobs_AsCandidate_Returns403() throws Exception {
        mockMvc.perform(get("/api/recruiter/jobs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void createJob_ReturnsCreatedJob() throws Exception {
        JobRequest request = new JobRequest(
            "Developer", "Full-stack role", "TechCorp", "Lyon",
            50000, 70000, "CDI", "Full-time", "Hybrid", 12
        );
        
        JobResponse response = new JobResponse(
            1L, "Developer", "Full-stack role", "TechCorp", "Lyon",
            50000, 70000, "CDI", "Full-time", "Hybrid", 12,
            true, LocalDateTime.now(), null, 1L, "John Recruiter"
        );
        
        when(recruiterService.createJob(any(JobRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/recruiter/jobs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Developer"));
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void updateJob_ReturnsUpdatedJob() throws Exception {
        JobRequest request = new JobRequest(
            "Lead Developer", "Updated desc", "TechCorp", "Paris",
            60000, 80000, "CDI", "Full-time", "Remote", 24
        );
        
        JobResponse response = new JobResponse(
            1L, "Lead Developer", "Updated desc", "TechCorp", "Paris",
            60000, 80000, "CDI", "Full-time", "Remote", 24,
            true, LocalDateTime.now(), null, 1L, "John Recruiter"
        );
        
        when(recruiterService.updateJob(eq(1L), any(JobRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/recruiter/jobs/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lead Developer"));
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void deleteJob_Returns204() throws Exception {
        mockMvc.perform(delete("/api/recruiter/jobs/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void getApplicationsForJob_ReturnsApplications() throws Exception {
        ApplicationResponse app = new ApplicationResponse(
            1L, 1L, "Developer", "TechCorp", "Paris",
            2L, "Jane Candidate", "PENDING", "Cover letter",
            LocalDateTime.now()
        );
        
        when(recruiterService.getApplicationsForJob(1L)).thenReturn(List.of(app));

        mockMvc.perform(get("/api/recruiter/jobs/1/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void updateApplicationStatus_ReturnsUpdatedApplication() throws Exception {
        ApplicationResponse app = new ApplicationResponse(
            1L, 1L, "Developer", "TechCorp", "Paris",
            2L, "Jane Candidate", "ACCEPTED", "Cover letter",
            LocalDateTime.now()
        );
        
        when(recruiterService.updateApplicationStatus(1L, "ACCEPTED")).thenReturn(app);

        mockMvc.perform(put("/api/recruiter/applications/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ACCEPTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void getJobs_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/recruiter/jobs"))
                .andExpect(status().isUnauthorized());
    }
}
