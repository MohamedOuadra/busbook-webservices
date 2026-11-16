package com.busbook.auth_service.entity;

import com.busbook.auth_service.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entité représentant un utilisateur de la plateforme BusBook.
 * Implémente UserDetails pour l'intégration avec Spring Security.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Adresse email unique (utilisée comme identifiant) */
    @Column(unique = true, nullable = false)
    private String email;

    /** Mot de passe haché (BCrypt), jamais sérialisé vers le frontend */
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** Nom complet de l'utilisateur */
    @Column(nullable = false)
    private String fullName;

    /** Numéro de téléphone */
    private String phoneNumber;

    /** Rôle de l'utilisateur (USER ou ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Date de création du compte */
    private LocalDateTime createdAt;

    /** Initialisation des champs par défaut avant persistance */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    // --- Implémentation UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
