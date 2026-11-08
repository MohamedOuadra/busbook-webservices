package com.busbook.reservation_service.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatValidationTest {

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
    void shouldAcceptValidSeat() {

        Seat seat = new Seat("A1", 1L);

        var violations = validator.validate(seat);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankSeatNumber() {

        Seat seat = new Seat("", 1L);

        var violations = validator.validate(seat);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullBusId() {

        Seat seat = new Seat("A1", null);

        var violations = validator.validate(seat);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNonPositiveBusId() {

        Seat seat = new Seat("A1", -1L);

        var violations = validator.validate(seat);

        assertFalse(violations.isEmpty());
    }
}