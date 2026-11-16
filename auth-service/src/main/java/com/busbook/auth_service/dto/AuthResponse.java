package com.busbook.auth_service.dto;

import com.busbook.auth_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO retourné après une authentification réussie (register ou login).
 * Contient le token JWT et les infos de base de l'utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private Role role;
}
