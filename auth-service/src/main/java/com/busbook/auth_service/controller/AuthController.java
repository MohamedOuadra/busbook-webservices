package com.busbook.auth_service.controller;

import com.busbook.auth_service.dto.AuthResponse;
import com.busbook.auth_service.dto.LoginRequest;
import com.busbook.auth_service.dto.RegisterRequest;
import com.busbook.auth_service.dto.UserResponse;
import com.busbook.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification et la gestion des comptes.
 * Routes publiques : /api/auth/register et /api/auth/login
 * Route protégée : /api/auth/me (nécessite un token JWT valide)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Crée un nouveau compte utilisateur.
     * POST /api/auth/register
     *
     * @param request les données d'inscription (validées)
     * @return 201 Created avec le token JWT et les infos utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authentifie un utilisateur existant.
     * POST /api/auth/login
     *
     * @param request les identifiants (email + password)
     * @return 200 OK avec le token JWT et les infos utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retourne les informations de l'utilisateur connecté.
     * GET /api/auth/me (nécessite un Bearer token valide)
     *
     * @param userDetails l'utilisateur extrait du token JWT
     * @return 200 OK avec les infos de l'utilisateur (sans mot de passe)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = authService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
