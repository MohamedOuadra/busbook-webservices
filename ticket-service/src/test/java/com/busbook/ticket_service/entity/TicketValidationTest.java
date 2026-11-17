package com.busbook.ticket_service.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenTicketIsValid() {
        Ticket ticket = new Ticket(
                "TCK-20261116-A1B2C3D4",
                1L,
                10L,
                "A5",
                "Ahmed Alami",
                "ahmed@example.com",
                "BUSBOOK|TCK-20261116-A1B2C3D4|RES:1|TRIP:10|SEAT:A5"
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenTicketNumberIsBlank() {
        Ticket ticket = new Ticket(
                "",
                1L,
                10L,
                "A5",
                "Ahmed",
                "ahmed@example.com",
                "QR-DATA"
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenReservationIdIsNull() {
        Ticket ticket = new Ticket(
                "TCK-100",
                null,
                10L,
                "A5",
                "Ahmed",
                "ahmed@example.com",
                "QR-DATA"
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenTripIdIsZeroOrNegative() {
        Ticket ticket = new Ticket(
                "TCK-100",
                1L,
                0L,
                "A5",
                "Ahmed",
                "ahmed@example.com",
                "QR-DATA"
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenSeatNumberIsBlank() {
        Ticket ticket = new Ticket(
                "TCK-100",
                1L,
                10L,
                " ",
                "Ahmed",
                "ahmed@example.com",
                "QR-DATA"
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenQrCodeIsBlank() {
        Ticket ticket = new Ticket(
                "TCK-100",
                1L,
                10L,
                "A5",
                "Ahmed",
                "ahmed@example.com",
                ""
        );

        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
    }
}
