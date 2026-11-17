package com.busbook.auth_service.controller;

import com.busbook.auth_service.dto.AuthResponse;
import com.busbook.auth_service.dto.LoginRequest;
import com.busbook.auth_service.dto.RegisterRequest;
import com.busbook.auth_service.dto.UserResponse;
import com.busbook.auth_service.enums.Role;
import com.busbook.auth_service.exception.GlobalExceptionHandler;
import com.busbook.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/auth/register - 201 Created avec succès")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Karim Alami");
        request.setEmail("karim@busbook.ma");
        request.setPassword("motdepasse123");
        request.setPhoneNumber("0611223344");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token-sample")
                .type("Bearer")
                .id(1L)
                .email("karim@busbook.ma")
                .fullName("Karim Alami")
                .role(Role.USER)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-sample"))
                .andExpect(jsonPath("$.email").value("karim@busbook.ma"))
                .andExpect(jsonPath("$.fullName").value("Karim Alami"));
    }

    @Test
    @DisplayName("POST /api/auth/register - 400 Bad Request si validation échoue")
    void testRegisterValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName(""); // vide
        request.setEmail("bad-email"); // email invalide
        request.setPassword("123"); // trop court (< 6)

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/register - 409 Conflict si email déjà existant")
    void testRegisterEmailConflict() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Karim Alami");
        request.setEmail("karim@busbook.ma");
        request.setPassword("motdepasse123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Cet email est déjà utilisé"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Cet email est déjà utilisé"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 200 OK avec succès")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("karim@busbook.ma");
        request.setPassword("motdepasse123");

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token-sample")
                .type("Bearer")
                .id(1L)
                .email("karim@busbook.ma")
                .fullName("Karim Alami")
                .role(Role.USER)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-sample"))
                .andExpect(jsonPath("$.email").value("karim@busbook.ma"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 401 Unauthorized si mauvais identifiants")
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("karim@busbook.ma");
        request.setPassword("wrong_password");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Email ou mot de passe incorrect"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
