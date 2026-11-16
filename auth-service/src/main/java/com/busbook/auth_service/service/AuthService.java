package com.busbook.auth_service.service;

import com.busbook.auth_service.dto.AuthResponse;
import com.busbook.auth_service.dto.LoginRequest;
import com.busbook.auth_service.dto.RegisterRequest;
import com.busbook.auth_service.dto.UserResponse;
import com.busbook.auth_service.entity.User;
import com.busbook.auth_service.enums.Role;
import com.busbook.auth_service.repository.UserRepository;
import com.busbook.auth_service.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service métier pour l'authentification et la gestion des comptes.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Crée un nouveau compte utilisateur et retourne un token JWT.
     *
     * @param request les données d'inscription
     * @return la réponse d'authentification avec le token
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    public AuthResponse register(RegisterRequest request) {
        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé : " + request.getEmail());
        }

        // Création et sauvegarde de l'utilisateur
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        // Génération du token JWT
        String token = jwtUtils.generateToken(savedUser.getEmail());

        return buildAuthResponse(savedUser, token);
    }

    /**
     * Authentifie un utilisateur existant et retourne un token JWT.
     *
     * @param request les identifiants de connexion
     * @return la réponse d'authentification avec le token
     * @throws BadCredentialsException si les identifiants sont incorrects
     */
    public AuthResponse login(LoginRequest request) {
        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        // Vérification du mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        // Génération du token JWT
        String token = jwtUtils.generateToken(user.getEmail());

        return buildAuthResponse(user, token);
    }

    /**
     * Retourne le profil utilisateur à partir de son email.
     *
     * @param email l'email de l'utilisateur
     * @return le profil UserResponse
     */
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /** Construit la réponse d'authentification à partir de l'utilisateur et du token */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
