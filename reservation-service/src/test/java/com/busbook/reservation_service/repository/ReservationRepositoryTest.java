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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Test
    void shouldDetectActiveReservationWhenPendingAndNotExpired() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B1", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(201L);
        reservation.setTravelerId(301L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(now.plusMinutes(5));
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(201L, seat.getId(), now);
        assertTrue(active);
    }

    @Test
    void shouldNotDetectActiveReservationWhenPendingAndExactlyExpired() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B2", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(202L);
        reservation.setTravelerId(302L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(now);
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(202L, seat.getId(), now);
        assertFalse(active);
    }

    @Test
    void shouldNotDetectActiveReservationWhenPendingAndExpiredBeforeScheduler() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B3", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(203L);
        reservation.setTravelerId(303L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(now.minusSeconds(10));
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(203L, seat.getId(), now);
        assertFalse(active);
    }

    @Test
    void shouldDetectActiveReservationWhenConfirmedEvenIfExpiresAtPassed() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B4", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(204L);
        reservation.setTravelerId(304L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservation.setExpiresAt(now.minusMinutes(10));
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(204L, seat.getId(), now);
        assertTrue(active);
    }

    @Test
    void shouldNotDetectActiveReservationWhenCancelled() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B5", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(205L);
        reservation.setTravelerId(305L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.ANNULEE);
        reservation.setExpiresAt(now.plusMinutes(5));
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(205L, seat.getId(), now);
        assertFalse(active);
    }

    @Test
    void shouldNotDetectActiveReservationWhenExpired() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B6", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation reservation = new Reservation();
        reservation.setTripId(206L);
        reservation.setTravelerId(306L);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EXPIREE);
        reservation.setExpiresAt(now.minusMinutes(1));
        reservationRepository.saveAndFlush(reservation);

        boolean active = reservationRepository.existsActiveReservation(206L, seat.getId(), now);
        assertFalse(active);
    }

    @Test
    void shouldAllowNewReservationAfterExpiration() {
        Seat seat = seatRepository.saveAndFlush(new Seat("B7", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Reservation firstReservation = new Reservation();
        firstReservation.setTripId(207L);
        firstReservation.setTravelerId(307L);
        firstReservation.setSeat(seat);
        firstReservation.setStatus(ReservationStatus.EN_ATTENTE);
        firstReservation.setExpiresAt(now.minusMinutes(5));
        reservationRepository.saveAndFlush(firstReservation);

        // Nouvelle réservation pour le même siège et le même trajet après expiration
        Reservation secondReservation = new Reservation();
        secondReservation.setTripId(207L);
        secondReservation.setTravelerId(308L);
        secondReservation.setSeat(seat);
        secondReservation.setStatus(ReservationStatus.EN_ATTENTE);
        secondReservation.setExpiresAt(now.plusMinutes(10));
        Reservation saved = reservationRepository.saveAndFlush(secondReservation);

        assertNotNull(saved.getId());
        assertEquals(ReservationStatus.EN_ATTENTE, saved.getStatus());
    }

    @Test
    void shouldExpirePendingReservationsForSpecificSeat() {
        Seat seat1 = seatRepository.saveAndFlush(new Seat("C1", 1L));
        Seat seat2 = seatRepository.saveAndFlush(new Seat("C2", 1L));
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // Réservation expirée sur le siège 1
        Reservation res1 = new Reservation();
        res1.setTripId(301L);
        res1.setTravelerId(401L);
        res1.setSeat(seat1);
        res1.setStatus(ReservationStatus.EN_ATTENTE);
        res1.setExpiresAt(now.minusMinutes(2));
        reservationRepository.saveAndFlush(res1);

        // Réservation expirée sur le siège 2 (même trajet)
        Reservation res2 = new Reservation();
        res2.setTripId(301L);
        res2.setTravelerId(402L);
        res2.setSeat(seat2);
        res2.setStatus(ReservationStatus.EN_ATTENTE);
        res2.setExpiresAt(now.minusMinutes(2));
        reservationRepository.saveAndFlush(res2);

        // Réservation non expirée sur le siège 1 d'un autre trajet
        Reservation res3 = new Reservation();
        res3.setTripId(302L);
        res3.setTravelerId(403L);
        res3.setSeat(seat1);
        res3.setStatus(ReservationStatus.EN_ATTENTE);
        res3.setExpiresAt(now.plusMinutes(5));
        reservationRepository.saveAndFlush(res3);

        int updated = reservationRepository.expirePendingReservationsForSeat(301L, seat1.getId(), now);

        assertEquals(1, updated);
        Reservation updatedRes1 = reservationRepository.findById(res1.getId()).orElseThrow();
        assertEquals(ReservationStatus.EXPIREE, updatedRes1.getStatus());

        Reservation unchangedRes2 = reservationRepository.findById(res2.getId()).orElseThrow();
        assertEquals(ReservationStatus.EN_ATTENTE, unchangedRes2.getStatus());

        Reservation unchangedRes3 = reservationRepository.findById(res3.getId()).orElseThrow();
        assertEquals(ReservationStatus.EN_ATTENTE, unchangedRes3.getStatus());
    }
}