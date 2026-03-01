package com.fij.services;

import com.fij.dto.ProfileRequest;
import com.fij.dto.ProfileResponse;
import com.fij.models.Role;
import com.fij.models.User;
import com.fij.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhone("1234567890");
        testUser.setLocation("Paris");
        testUser.setRoles(Set.of(Role.ROLE_CANDIDATE));

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("test@test.com", null, List.of())
        );
    }

    @Test
    void getCurrentUser_ReturnsProfile() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        ProfileResponse response = userService.getCurrentUser();

        assertEquals("test@test.com", response.email());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
    }

    @Test
    void getCurrentUser_NotFound_ThrowsException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.getCurrentUser());
    }

    @Test
    void updateProfile_Success_UpdatesFields() {
        ProfileRequest request = new ProfileRequest("Jane", "Smith", "0987654321", "Lyon");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ProfileResponse response = userService.updateProfile(request);

        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
        assertEquals("Lyon", response.location());
    }

    @Test
    void updateProfile_PartialUpdate_OnlyUpdatesProvidedFields() {
        ProfileRequest request = new ProfileRequest(null, "NewName", null, null);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ProfileResponse response = userService.updateProfile(request);

        assertEquals("John", response.firstName());
        assertEquals("NewName", response.lastName());
    }
}
