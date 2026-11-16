package com.busbook.auth_service.service;

import com.busbook.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implémentation de UserDetailsService pour charger les utilisateurs
 * depuis la base de données lors de l'authentification.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge un utilisateur par son email (utilisé comme username).
     *
     * @param email l'email de l'utilisateur
     * @return les détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException si l'email n'existe pas en base
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));
    }
}
