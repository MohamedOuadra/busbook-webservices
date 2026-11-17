package com.busbook.auth_service.dto;

import com.busbook.auth_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO retourné pour les informations de profil utilisateur (/api/auth/me).
 * Ne contient jamais le mot de passe hashé.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private LocalDateTime createdAt;
}
