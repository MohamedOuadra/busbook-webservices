
package com.busbook.catalog.repository;

import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.entity.Trajet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TrajetRepositoryTest {

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private BusRepository busRepository;

    @Test
    void shouldSaveAndFindTrajetByCities() {
        // Arrange : enregistrer le bus avant le trajet
        Bus bus = new Bus("TRJ-123", 50, "STANDARD");
        busRepository.saveAndFlush(bus);

        LocalDateTime depart = LocalDateTime.of(2026, 11, 15, 8, 0);
        LocalDateTime arrivee = depart.plusHours(3);

        Trajet trajet = new Trajet(
                "Agadir",
                "Marrakech",
                depart,
                arrivee,
                new BigDecimal("120.00"),
                bus
        );

        // Act
        trajetRepository.saveAndFlush(trajet);

        var resultats = trajetRepository
                .findByVilleDepartAndVilleArrivee("Agadir", "Marrakech");

        // Assert
        assertEquals(1, resultats.size());
        assertEquals(
                0,
                new BigDecimal("120.00").compareTo(resultats.get(0).getPrix())
        );
        assertEquals(bus.getId(), resultats.get(0).getBus().getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrajetMatchesCities() {
        // Act
        var resultats = trajetRepository
                .findByVilleDepartAndVilleArrivee("Agadir", "Casablanca");

        // Assert
        assertTrue(resultats.isEmpty());
    }

}
