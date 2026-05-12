package com.fij;

import com.fij.models.Role;
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
class Phase1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_Success_ReturnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void register_DuplicateEmail_ThrowsError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123","firstName":"Jane","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login_Success_ReturnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_RECRUITER"]}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_WrongPassword_ThrowsError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@test.com","password":"wrongpassword"}
                    """))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getProfile_Authenticated_ReturnsProfile() throws Exception {
        String token = registerAndLogin("candidate@test.com");

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("candidate@test.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getProfile_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProfile_Success_UpdatesFields() throws Exception {
        String token = registerAndLogin("test@test.com");

        mockMvc.perform(put("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"Jane","lastName":"Smith","phone":"1234567890","location":"Paris"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.location").value("Paris"));
    }

    @Test
    void updateProfile_PartialUpdate_OnlyUpdatesProvided() throws Exception {
        String token = registerAndLogin("test@test.com");

        mockMvc.perform(put("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""                                                                                                                                                              
                    {"lastName":"NewName"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("NewName"));
    }

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {"email":"%s","password":"password123","firstName":"John","lastName":"Doe","roles":["ROLE_CANDIDATE"]}
                    """, email)))
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
