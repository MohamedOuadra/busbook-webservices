package com.busbook.auth_service.repository;

import com.busbook.auth_service.entity.User;
import com.busbook.auth_service.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Sauvegarder et trouver un utilisateur par son email")
    void testSaveAndFindByEmail() {
        User user = User.builder()
                .fullName("Karim Alami")
                .email("karim@busbook.ma")
                .password("encoded_pass")
                .phoneNumber("0612345678")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("karim@busbook.ma");
        assertTrue(found.isPresent());
        assertEquals("Karim Alami", found.get().getFullName());
        assertEquals(Role.USER, found.get().getRole());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @DisplayName("Vérifier l'existence d'un email")
    void testExistsByEmail() {
        User user = User.builder()
                .fullName("Sara Bennani")
                .email("sara@busbook.ma")
                .password("encoded_pass")
                .phoneNumber("0698765432")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("sara@busbook.ma"));
        assertFalse(userRepository.existsByEmail("nonexistant@busbook.ma"));
    }
}
