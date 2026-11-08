
package com.busbook.catalog.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrajetValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private Trajet creerTrajetValide() {
        Bus bus = new Bus("12345-A-6", 50, "STANDARD");

        LocalDateTime depart =
                LocalDateTime.of(2026, 11, 15, 8, 0);

        LocalDateTime arrivee = depart.plusHours(3);

        return new Trajet(
                "Agadir",
                "Marrakech",
                depart,
                arrivee,
                new BigDecimal("120.00"),
                bus
        );
    }

    @Test
    void shouldAcceptValidTrajet() {
        Trajet trajet = creerTrajetValide();

        var violations = validator.validate(trajet);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankDepartureCity() {
        Trajet trajet = creerTrajetValide();
        trajet.setVilleDepart(" ");

        var violations = validator.validate(trajet);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("villeDepart")
        ));
    }

    @Test
    void shouldRejectNegativePrice() {
        Trajet trajet = creerTrajetValide();
        trajet.setPrix(new BigDecimal("-20.00"));

        var violations = validator.validate(trajet);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("prix")
        ));
    }

    @Test
    void shouldRejectMissingBus() {
        Trajet trajet = creerTrajetValide();
        trajet.setBus(null);

        var violations = validator.validate(trajet);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("bus")
        ));
    }

    @Test
    void shouldRejectMissingDepartureDate() {
        Trajet trajet = creerTrajetValide();
        trajet.setDateDepart(null);

        var violations = validator.validate(trajet);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("dateDepart")
        ));
    }

    @Test
    void shouldRejectArrivalBeforeDeparture() {
        // Arrange
        Trajet trajet = creerTrajetValide();
        trajet.setDateArrivee(
                trajet.getDateDepart().minusHours(1)
        );

        // Act
        var violations = validator.validate(trajet);

        // Assert
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString()
                        .equals("chronologieValide")
        ));
    }

    @Test
    void shouldRejectArrivalEqualToDeparture() {
        // Arrange
        Trajet trajet = creerTrajetValide();
        trajet.setDateArrivee(trajet.getDateDepart());

        // Act
        var violations = validator.validate(trajet);

        // Assert
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString()
                        .equals("chronologieValide")
        ));
    }

    @Test
    void shouldRejectSameDepartureAndArrivalCity() {
        // Arrange
        Trajet trajet = creerTrajetValide();
        trajet.setVilleDepart("Agadir");
        trajet.setVilleArrivee("Agadir");

        // Act
        var violations = validator.validate(trajet);

        // Assert
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString()
                        .equals("villesDifferentes")
        ));
    }

    @Test
    void shouldRejectSameCityIgnoringCaseAndSpaces() {
        // Arrange
        Trajet trajet = creerTrajetValide();
        trajet.setVilleDepart(" Agadir ");
        trajet.setVilleArrivee("agadir");

        // Act
        var violations = validator.validate(trajet);

        // Assert
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString()
                        .equals("villesDifferentes")
        ));
    }


}
