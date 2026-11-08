
package com.busbook.catalog.repository;

import com.busbook.catalog.entity.Bus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BusRepositoryTest {

    @Autowired
    private BusRepository busRepository;

    @Test
    void shouldSaveAndFindBusByImmatriculation() {
        // Arrange
        Bus bus = new Bus("TEST-123", 50, "STANDARD");

        // Act
        busRepository.saveAndFlush(bus);
        var resultat = busRepository.findByImmatriculation("TEST-123");

        // Assert
        assertTrue(resultat.isPresent());
        assertEquals(50, resultat.get().getCapacite());
    }

    @Test
    void shouldDetectExistingImmatriculation() {
        // Arrange
        Bus bus = new Bus("TEST-456", 40, "VIP");
        busRepository.saveAndFlush(bus);

        // Act
        boolean existe = busRepository.existsByImmatriculation("TEST-456");

        // Assert
        assertTrue(existe);
    }

    @Test
    void shouldReturnEmptyForUnknownImmatriculation() {
        // Act
        var resultat = busRepository.findByImmatriculation("INCONNU-999");

        // Assert
        assertTrue(resultat.isEmpty());
    }

    @Test
    void shouldRejectDuplicateImmatriculation() {
        Bus premierBus = new Bus("DUP-123", 50, "STANDARD");
        Bus deuxiemeBus = new Bus("DUP-123", 40, "VIP");

        busRepository.saveAndFlush(premierBus);

        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> busRepository.saveAndFlush(deuxiemeBus)
        );
    }

}
