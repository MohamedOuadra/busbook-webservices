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
 * Route protégée  : /api/auth/me (nécessite un Bearer token valide)
 * Route admin     : /api/auth/make-admin (promouvoir un utilisateur en ADMIN)
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
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authentifie un utilisateur existant.
     * POST /api/auth/login
     * La réponse contient le token JWT avec le rôle encodé.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retourne les informations de l'utilisateur connecté.
     * GET /api/auth/me (nécessite un Bearer token valide)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = authService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Promeut un utilisateur au rôle ADMIN.
     * POST /api/auth/make-admin?email=user@example.com
     *
     * Endpoint non protégé par Spring Security pour faciliter le développement.
     * En production, cet endpoint devrait être protégé par un secret ou supprimé.
     */
    @PostMapping("/make-admin")
    public ResponseEntity<UserResponse> makeAdmin(@RequestParam String email) {
        UserResponse response = authService.makeAdmin(email);
        return ResponseEntity.ok(response);
    }
}
