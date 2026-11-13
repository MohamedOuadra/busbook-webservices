package com.busbook.reservation_service.repository;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.entity.Seat;
import com.busbook.reservation_service.enums.ReservationStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")

class ReservationRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void shouldSaveAndFindReservation() {

        // 1. Créer et enregistrer un siège
        Seat seat = new Seat("A1", 1L);
        seat = seatRepository.saveAndFlush(seat);

        // 2. Créer une réservation
        Reservation reservation = new Reservation();

        reservation.setTripId(100L);
        reservation.setTravelerId(200L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        // 3. Enregistrer la réservation
        reservationRepository.saveAndFlush(reservation);

        // 4. Vérifier la récupération
        var result = reservationRepository.findByTripId(100L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        Reservation savedReservation = result.get(0);

        assertEquals(200L, savedReservation.getTravelerId());
        assertEquals(ReservationStatus.EN_ATTENTE,
                savedReservation.getStatus());
        assertEquals("A1", savedReservation.getSeat().getSeatNumber());
    }

//    @Test
//    void shouldAllowNewReservationAfterCancellation() {
//
//        // 1. Créer et sauvegarder un siège
//        Seat seat = new Seat("A1", 1L);
//        seat = seatRepository.saveAndFlush(seat);
//
//        // 2. Créer la première réservation
//        Reservation firstReservation = new Reservation();
//        firstReservation.setTripId(100L);
//        firstReservation.setTravelerId(200L);
//        firstReservation.setSeat(seat);
//        firstReservation.setStatus(ReservationStatus.EN_ATTENTE);
//
//        reservationRepository.saveAndFlush(firstReservation);
//
//        // 3. Annuler la première réservation
//        firstReservation.setStatus(ReservationStatus.ANNULEE);
//        reservationRepository.saveAndFlush(firstReservation);
//
//        // 4. Créer une deuxième réservation
//        // pour le même siège et le même trajet
//        Reservation secondReservation = new Reservation();
//        secondReservation.setTripId(100L);
//        secondReservation.setTravelerId(300L);
//        secondReservation.setSeat(seat);
//        secondReservation.setStatus(ReservationStatus.EN_ATTENTE);
//
//        Reservation savedReservation =
//                reservationRepository.saveAndFlush(secondReservation);
//
//        // 5. Vérifier le résultat
//        assertNotNull(savedReservation.getId());
//        assertEquals(
//                ReservationStatus.EN_ATTENTE,
//                savedReservation.getStatus()
//        );
//    }

    @Test
    void shouldFindReservationsByTravelerId() {

        Seat seat = seatRepository.saveAndFlush(
                new Seat("A2", 1L)
        );

        Reservation reservation = new Reservation();
        reservation.setTripId(101L);
        reservation.setTravelerId(201L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        reservationRepository.saveAndFlush(reservation);

        var result = reservationRepository.findByTravelerId(201L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getTripId());
    }

    @Test
    void shouldFindReservationsByTripIdAndStatus() {

        Seat seat = seatRepository.saveAndFlush(
                new Seat("A3", 1L)
        );

        Reservation reservation = new Reservation();
        reservation.setTripId(102L);
        reservation.setTravelerId(202L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        reservationRepository.saveAndFlush(reservation);

        var result = reservationRepository.findByTripIdAndStatus(
                102L,
                ReservationStatus.CONFIRMEE
        );

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(202L, result.get(0).getTravelerId());
    }

    @Test
    void shouldDetectActiveReservation() {

        Seat seat = seatRepository.saveAndFlush(
                new Seat("A4", 1L)
        );

        Reservation reservation = new Reservation();
        reservation.setTripId(103L);
        reservation.setTravelerId(203L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        reservationRepository.saveAndFlush(reservation);

        boolean result =
                reservationRepository.existsByTripIdAndSeat_IdAndStatusIn(
                        103L,
                        seat.getId(),
                        List.of(
                                ReservationStatus.EN_ATTENTE,
                                ReservationStatus.CONFIRMEE
                        )
                );

        assertTrue(result);
    }

    @Test
    void shouldDetectDuplicateActiveReservationForSameTripAndSeat() {

        // 1. Créer un siège
        Seat seat = seatRepository.saveAndFlush(
                new Seat("A5", 1L)
        );

        // 2. Créer une première réservation active
        Reservation firstReservation = new Reservation();
        firstReservation.setTripId(104L);
        firstReservation.setTravelerId(204L);
        firstReservation.setSeat(seat);
        firstReservation.setStatus(ReservationStatus.EN_ATTENTE);

        reservationRepository.saveAndFlush(firstReservation);

        // 3. Vérifier que le repository détecte la réservation active
        boolean exists = reservationRepository
                .existsByTripIdAndSeat_IdAndStatusIn(
                        104L,
                        seat.getId(),
                        List.of(
                                ReservationStatus.EN_ATTENTE,
                                ReservationStatus.CONFIRMEE
                        )
                );

        assertTrue(exists);
    }

    @Test
    void shouldAllowNewReservationAfterCancellation() {

        Seat seat = seatRepository.saveAndFlush(
                new Seat("A6", 1L)
        );

        // Première réservation
        Reservation firstReservation = new Reservation();
        firstReservation.setTripId(105L);
        firstReservation.setTravelerId(206L);
        firstReservation.setSeat(seat);
        firstReservation.setStatus(ReservationStatus.EN_ATTENTE);

        firstReservation = reservationRepository.saveAndFlush(firstReservation);

        // Annuler la première réservation
        firstReservation.setStatus(ReservationStatus.ANNULEE);
        reservationRepository.saveAndFlush(firstReservation);

        // Deuxième réservation pour le même siège et trajet
        Reservation secondReservation = new Reservation();
        secondReservation.setTripId(105L);
        secondReservation.setTravelerId(207L);
        secondReservation.setSeat(seat);
        secondReservation.setStatus(ReservationStatus.EN_ATTENTE);

        Reservation savedReservation =
                reservationRepository.saveAndFlush(secondReservation);

        assertNotNull(savedReservation.getId());
        assertEquals(
                ReservationStatus.EN_ATTENTE,
                savedReservation.getStatus()
        );
    }
}