package com.busbook.auth_service.service;

import com.busbook.auth_service.dto.AuthResponse;
import com.busbook.auth_service.dto.LoginRequest;
import com.busbook.auth_service.dto.RegisterRequest;
import com.busbook.auth_service.dto.UserResponse;
import com.busbook.auth_service.entity.User;
import com.busbook.auth_service.enums.Role;
import com.busbook.auth_service.repository.UserRepository;
import com.busbook.auth_service.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .fullName("Amine Tazi")
                .email("amine@busbook.ma")
                .password("hashed_password_123")
                .phoneNumber("0600112233")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Inscription réussie d'un nouvel utilisateur")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Amine Tazi");
        request.setEmail("amine@busbook.ma");
        request.setPassword("plain_password");
        request.setPhoneNumber("0600112233");

        when(userRepository.existsByEmail("amine@busbook.ma")).thenReturn(false);
        when(passwordEncoder.encode("plain_password")).thenReturn("hashed_password_123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtils.generateToken("amine@busbook.ma")).thenReturn("jwt-token-123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("amine@busbook.ma", response.getEmail());
        assertEquals("Amine Tazi", response.getFullName());
        assertEquals(Role.USER, response.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Inscription échouée si l'email existe déjà")
    void testRegisterEmailConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Amine Tazi");
        request.setEmail("amine@busbook.ma");
        request.setPassword("plain_password");

        when(userRepository.existsByEmail("amine@busbook.ma")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Connexion réussie avec identifiants valides")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("amine@busbook.ma");
        request.setPassword("plain_password");

        when(userRepository.findByEmail("amine@busbook.ma")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plain_password", "hashed_password_123")).thenReturn(true);
        when(jwtUtils.generateToken("amine@busbook.ma")).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("amine@busbook.ma", response.getEmail());
    }

    @Test
    @DisplayName("Connexion échouée avec mauvais mot de passe")
    void testLoginBadPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("amine@busbook.ma");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail("amine@busbook.ma")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_password", "hashed_password_123")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Connexion échouée avec email inexistant")
    void testLoginEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@busbook.ma");
        request.setPassword("password");

        when(userRepository.findByEmail("inconnu@busbook.ma")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Récupérer le profil utilisateur par email")
    void testGetUserProfile() {
        when(userRepository.findByEmail("amine@busbook.ma")).thenReturn(Optional.of(sampleUser));

        UserResponse profile = authService.getUserProfile("amine@busbook.ma");

        assertNotNull(profile);
        assertEquals("amine@busbook.ma", profile.getEmail());
        assertEquals("Amine Tazi", profile.getFullName());
        assertEquals(Role.USER, profile.getRole());
    }
}
