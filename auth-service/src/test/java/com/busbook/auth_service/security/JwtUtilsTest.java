package com.busbook.auth_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testSecret = "busbook-super-secret-key-for-testing-at-least-32-chars";
    private final long testExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "expirationMs", testExpirationMs);
    }

    @Test
    @DisplayName("Générer un token JWT valide et extraire l'email")
    void testGenerateAndExtractUsername() {
        String email = "test@busbook.ma";
        String token = jwtUtils.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String extracted = jwtUtils.extractUsername(token);
        assertEquals(email, extracted);
    }

    @Test
    @DisplayName("Valider un token correctement signé")
    void testIsTokenValid() {
        String email = "voyageur@busbook.ma";
        String token = jwtUtils.generateToken(email);

        assertTrue(jwtUtils.isTokenValid(token));
    }

    @Test
    @DisplayName("Rejeter un token altéré ou invalide")
    void testInvalidToken() {
        assertFalse(jwtUtils.isTokenValid("token-invalide-non-jwt"));
        assertFalse(jwtUtils.isTokenValid(""));
    }

    @Test
    @DisplayName("Rejeter un token expiré")
    void testExpiredToken() {
        ReflectionTestUtils.setField(jwtUtils, "expirationMs", -1000L); // expiré dans le passé
        String expiredToken = jwtUtils.generateToken("expired@busbook.ma");

        assertFalse(jwtUtils.isTokenValid(expiredToken));
    }
}
