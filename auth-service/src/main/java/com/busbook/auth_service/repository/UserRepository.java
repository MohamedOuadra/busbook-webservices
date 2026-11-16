package com.busbook.auth_service.repository;

import com.busbook.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour la gestion des utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Cherche un utilisateur par son adresse email */
    Optional<User> findByEmail(String email);

    /** Vérifie si un email est déjà utilisé */
    boolean existsByEmail(String email);
}
