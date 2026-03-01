package com.fij.services;

import com.fij.dto.AuthResponse;
import com.fij.dto.LoginRequest;
import com.fij.dto.RegisterRequest;
import com.fij.models.Role;
import com.fij.models.User;
import com.fij.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void register_Success_ReturnsToken() {
        RegisterRequest request = new RegisterRequest(
            "test@test.com", "password", "John", "Doe", null, null, Set.of(Role.ROLE_CANDIDATE)
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test@test.com", response.email());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("jwtToken", response.token());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest(
            "test@test.com", "password", "John", "Doe", null, null, Set.of(Role.ROLE_CANDIDATE)
        );

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    void login_Success_ReturnsToken() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoles(Set.of(Role.ROLE_CANDIDATE));

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test@test.com", response.email());
        assertEquals("jwtToken", response.token());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest("notfound@test.com", "password");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authService.login(request));
    }
}
