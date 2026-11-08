
package com.busbook.catalog.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusValidationTest {

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

    @Test
    void shouldAcceptValidBus() {
        Bus bus = new Bus("12345-A-6", 50, "STANDARD");

        var violations = validator.validate(bus);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectNegativeCapacity() {
        Bus bus = new Bus("12345-A-6", -10, "STANDARD");

        var violations = validator.validate(bus);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("capacite")
        ));
    }

    @Test
    void shouldRejectBlankRegistration() {
        Bus bus = new Bus("   ", 50, "STANDARD");

        var violations = validator.validate(bus);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("immatriculation")
        ));
    }

    @Test
    void shouldRejectZeroCapacity() {
        Bus bus = new Bus("12345-A-6", 0, "STANDARD");

        var violations = validator.validate(bus);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("capacite")
        ));
    }
}
