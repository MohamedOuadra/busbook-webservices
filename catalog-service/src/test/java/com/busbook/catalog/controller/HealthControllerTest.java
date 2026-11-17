
package com.busbook.catalog.controller;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void shouldReturnServiceStatusUp() {
        // Arrange : préparer le contrôleur
        HealthController controller = new HealthController();

        // Act : appeler la méthode
        Map<String, String> response = controller.health();

        // Assert : vérifier le résultat
        assertEquals("catalog-service", response.get("service"));
        assertEquals("UP", response.get("status"));
    }
}
