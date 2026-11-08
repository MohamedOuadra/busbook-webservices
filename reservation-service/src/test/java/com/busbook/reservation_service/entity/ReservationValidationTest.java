package com.busbook.reservation_service.entity;

import com.busbook.reservation_service.enums.ReservationStatus;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationValidationTest {

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
    void shouldAcceptValidReservation() {

        Seat seat = new Seat("A1", 1L);

        Reservation reservation = new Reservation();
        reservation.setTripId(100L);
        reservation.setTravelerId(200L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        var violations = validator.validate(reservation);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectNullTripId() {

        Reservation reservation = new Reservation();
        reservation.setTripId(null);
        reservation.setTravelerId(200L);
        reservation.setSeat(new Seat("A1", 1L));
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        var violations = validator.validate(reservation);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNegativeTravelerId() {

        Reservation reservation = new Reservation();
        reservation.setTripId(100L);
        reservation.setTravelerId(-1L);
        reservation.setSeat(new Seat("A1", 1L));
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        var violations = validator.validate(reservation);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullSeat() {

        Reservation reservation = new Reservation();
        reservation.setTripId(100L);
        reservation.setTravelerId(200L);
        reservation.setSeat(null);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        var violations = validator.validate(reservation);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldRejectNullStatus() {

        Reservation reservation = new Reservation();
        reservation.setTripId(100L);
        reservation.setTravelerId(200L);
        reservation.setSeat(new Seat("A1", 1L));
        reservation.setStatus(null);

        var violations = validator.validate(reservation);

        assertFalse(violations.isEmpty());
    }
}
