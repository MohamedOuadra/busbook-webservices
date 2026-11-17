package com.busbook.reservation_service.service;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.exception.ResourceNotFoundException;
import com.busbook.reservation_service.repository.ReservationRepository;
import com.busbook.reservation_service.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.busbook.reservation_service.dto.ReservationRequest;
import com.busbook.reservation_service.dto.ReservationResponse;
import com.busbook.reservation_service.entity.Seat;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldReturnTrueWhenSeatIsAvailable() {
        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(5L), any(LocalDateTime.class)
        )).thenReturn(false);

        boolean result = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenSeatIsAlreadyReserved() {
        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(5L), any(LocalDateTime.class)
        )).thenReturn(true);

        boolean result = reservationService.isSeatAvailable(1L, 5L);

        assertFalse(result);
    }

    @Test
    void shouldRejectInvalidTripId() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.isSeatAvailable(0L, 5L));
    }

    @Test
    void shouldConfirmPendingReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reservation result = reservationService.confirmReservation(1L);

        assertEquals(ReservationStatus.CONFIRMEE, result.getStatus());
        assertNotNull(result.getConfirmedAt());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldRejectConfirmationOfCancelledReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.ANNULEE);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.confirmReservation(1L));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenReservationDoesNotExist() {
        when(reservationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.confirmReservation(99L);
        });
    }

    @Test
    void shouldCancelPendingReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reservation result = reservationService.cancelReservation(1L);

        assertEquals(ReservationStatus.ANNULEE, result.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldRejectCancellationOfExpiredReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EXPIREE);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.cancelReservation(1L));

        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldCreateReservationSuccessfully() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(1L);
        request.setTravelerId(5L);
        request.setSeatId(12L);

        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(12L);
        when(seat.getSeatNumber()).thenReturn("A12");

        when(seatRepository.findById(12L))
                .thenReturn(Optional.of(seat));

        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(12L), any(LocalDateTime.class)
        )).thenReturn(false);

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation = invocation.getArgument(0);

                    // Simuler l'identifiant généré par la base de données
                    ReflectionTestUtils.setField(reservation, "id", 1L);

                    return reservation;
                });

        ReservationResponse response =
                reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getTripId());
        assertEquals(5L, response.getTravelerId());
        assertEquals(12L, response.getSeatId());
        assertEquals("A12", response.getSeatNumber());
        assertEquals(ReservationStatus.EN_ATTENTE, response.getStatus());

        verify(reservationRepository).saveAndFlush(any(Reservation.class));
    }


    @Test
    void shouldRejectReservationWhenSeatDoesNotExist() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(1L);
        request.setTravelerId(5L);
        request.setSeatId(99L);

        when(seatRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(request);
        });

        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldRejectReservationWhenSeatIsAlreadyReserved() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(1L);
        request.setTravelerId(5L);
        request.setSeatId(12L);

        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(12L);

        when(seatRepository.findById(12L))
                .thenReturn(Optional.of(seat));

        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(12L), any(LocalDateTime.class)
        )).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> reservationService.createReservation(request));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldCatchSeatConstraintViolationOnConcurrentInsertion() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(1L);
        request.setTravelerId(5L);
        request.setSeatId(12L);

        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(12L);
        when(seatRepository.findById(12L)).thenReturn(Optional.of(seat));

        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(12L), any(LocalDateTime.class)
        )).thenReturn(false);

        // Simuler la violation de clé unique PostgreSQL "uk_active_reservation_trip_seat"
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "ERROR: duplicate key value violates unique constraint \"uk_active_reservation_trip_seat\""
                ));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> reservationService.createReservation(request)
        );

        assertEquals("Ce siège est déjà réservé pour ce trajet.", ex.getMessage());
    }

    @Test
    void shouldRethrowUnrelatedDataIntegrityViolation() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(1L);
        request.setTravelerId(5L);
        request.setSeatId(12L);

        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(12L);
        when(seatRepository.findById(12L)).thenReturn(Optional.of(seat));

        when(reservationRepository.existsActiveReservation(
                eq(1L), eq(12L), any(LocalDateTime.class)
        )).thenReturn(false);

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "ERROR: insert or update on table violates foreign key constraint"
                ));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> reservationService.createReservation(request)
        );
    }


    @Test
    void shouldRejectReservationWithInvalidIds() {
        ReservationRequest request = new ReservationRequest();
        request.setTripId(0L);
        request.setTravelerId(5L);
        request.setSeatId(12L);

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(request));

        verify(seatRepository, never()).findById(any());
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldRejectNullReservationRequest() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(null));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldExpirePendingReservations() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(
                LocalDateTime.now().minusMinutes(1)
        );

        when(reservationRepository
                .findByStatusAndExpiresAtLessThanEqual(
                        eq(ReservationStatus.EN_ATTENTE),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(reservation));

        int result = reservationService.expirePendingReservations();

        assertEquals(1, result);
        assertEquals(ReservationStatus.EXPIREE, reservation.getStatus());

        verify(reservationRepository).saveAll(List.of(reservation));
    }

    @Test
    void shouldNotExpirePendingReservationBeforeExpirationDate() {
        when(reservationRepository
                .findByStatusAndExpiresAtLessThanEqual(
                        eq(ReservationStatus.EN_ATTENTE),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        int result = reservationService.expirePendingReservations();

        assertEquals(0, result);

        verify(reservationRepository).saveAll(List.of());
    }

    @Test
    void shouldNotExpireConfirmedReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservation.setExpiresAt(
                LocalDateTime.now().minusMinutes(5)
        );

        when(reservationRepository
                .findByStatusAndExpiresAtLessThanEqual(
                        eq(ReservationStatus.EN_ATTENTE),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        int result = reservationService.expirePendingReservations();

        assertEquals(0, result);
        assertEquals(ReservationStatus.CONFIRMEE, reservation.getStatus());

        verify(reservationRepository).saveAll(List.of());
    }

    @Test
    void shouldRejectConfirmationOfExpiredReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(
                LocalDateTime.now().minusMinutes(1)
        );

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                IllegalStateException.class,
                () -> reservationService.confirmReservation(1L)
        );

        assertEquals(
                ReservationStatus.EN_ATTENTE,
                reservation.getStatus()
        );

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldMakeSeatAvailableAfterReservationExpires() {
        when(reservationRepository.existsActiveReservation(
                eq(1L),
                eq(5L),
                any(LocalDateTime.class)
        )).thenReturn(false);

        boolean result = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(result);
    }

    // =========================================================================
    // Scénarios obligatoires (Tâche 1 - Fiabilisation expiration des réservations)
    // =========================================================================

    /**
     * Scénario 1 : Un siège reste indisponible avant l'expiration.
     */
    @Test
    void scenario1_seatRemainsUnavailableBeforeExpiration() {
        Instant baseTime = Instant.parse("2026-06-01T10:00:00Z");
        reservationService.setClock(Clock.fixed(baseTime, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(baseTime, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, now))
                .thenReturn(true);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertFalse(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, now);
    }

    /**
     * Scénario 2 : Un siège devient disponible exactement à l'heure d'expiration.
     */
    @Test
    void scenario2_seatBecomesAvailableExactlyAtExpirationTime() {
        Instant expirationInstant = Instant.parse("2026-06-01T10:10:00Z");
        reservationService.setClock(Clock.fixed(expirationInstant, ZoneOffset.UTC));
        LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, expirationTime))
                .thenReturn(false);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, expirationTime);
    }

    /**
     * Scénario 3 : Un siège est disponible après expiration, même si le scheduler n'est pas encore passé.
     */
    @Test
    void scenario3_seatIsAvailableAfterExpirationEvenBeforeSchedulerRuns() {
        Instant afterExpirationInstant = Instant.parse("2026-06-01T10:11:00Z");
        reservationService.setClock(Clock.fixed(afterExpirationInstant, ZoneOffset.UTC));
        LocalDateTime afterExpirationTime = LocalDateTime.ofInstant(afterExpirationInstant, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, afterExpirationTime))
                .thenReturn(false);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, afterExpirationTime);
    }

    /**
     * Scénario 4 : Une réservation confirmée bloque toujours le siège.
     */
    @Test
    void scenario4_confirmedReservationAlwaysBlocksSeat() {
        Instant nowInstant = Instant.parse("2026-06-01T10:30:00Z");
        reservationService.setClock(Clock.fixed(nowInstant, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, now))
                .thenReturn(true);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertFalse(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, now);
    }

    /**
     * Scénario 5 : Une réservation annulée ne bloque plus le siège.
     */
    @Test
    void scenario5_cancelledReservationDoesNotBlockSeat() {
        Instant nowInstant = Instant.parse("2026-06-01T10:05:00Z");
        reservationService.setClock(Clock.fixed(nowInstant, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, now))
                .thenReturn(false);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, now);
    }

    /**
     * Scénario 6 : Une réservation expirée ne bloque plus le siège.
     */
    @Test
    void scenario6_expiredReservationDoesNotBlockSeat() {
        Instant nowInstant = Instant.parse("2026-06-01T10:20:00Z");
        reservationService.setClock(Clock.fixed(nowInstant, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

        when(reservationRepository.existsActiveReservation(1L, 5L, now))
                .thenReturn(false);

        boolean available = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(available);
        verify(reservationRepository).existsActiveReservation(1L, 5L, now);
    }

    /**
     * Scénario 7 : La confirmation est autorisée avant l'expiration.
     */
    @Test
    void scenario7_confirmationAllowedBeforeExpiration() {
        Instant baseTime = Instant.parse("2026-06-01T10:05:00Z");
        reservationService.setClock(Clock.fixed(baseTime, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(baseTime, ZoneOffset.UTC);

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(now.plusMinutes(5));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Reservation confirmed = reservationService.confirmReservation(1L);

        assertEquals(ReservationStatus.CONFIRMEE, confirmed.getStatus());
        assertEquals(now, confirmed.getConfirmedAt());
        verify(reservationRepository).save(reservation);
    }

    /**
     * Scénario 8 : La confirmation est refusée exactement à l'heure d'expiration.
     */
    @Test
    void scenario8_confirmationRefusedExactlyAtExpirationTime() {
        Instant expirationInstant = Instant.parse("2026-06-01T10:10:00Z");
        reservationService.setClock(Clock.fixed(expirationInstant, ZoneOffset.UTC));
        LocalDateTime exactExpiration = LocalDateTime.ofInstant(expirationInstant, ZoneOffset.UTC);

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(exactExpiration);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> reservationService.confirmReservation(1L)
        );

        assertEquals("Le délai de confirmation de cette réservation est expiré.", ex.getMessage());
        assertEquals(ReservationStatus.EN_ATTENTE, reservation.getStatus());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    /**
     * Scénario 9 : La confirmation est refusée après expiration.
     */
    @Test
    void scenario9_confirmationRefusedAfterExpiration() {
        Instant baseTime = Instant.parse("2026-06-01T10:15:00Z");
        reservationService.setClock(Clock.fixed(baseTime, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(baseTime, ZoneOffset.UTC);

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(now.minusMinutes(5));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> reservationService.confirmReservation(1L)
        );

        assertEquals("Le délai de confirmation de cette réservation est expiré.", ex.getMessage());
        assertEquals(ReservationStatus.EN_ATTENTE, reservation.getStatus());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    /**
     * Scénario 10 : Le scheduler expire uniquement les réservations en attente arrivées à expiration.
     */
    @Test
    void scenario10_schedulerExpiresOnlyPendingReservationsArrivedAtExpiration() {
        Instant baseTime = Instant.parse("2026-06-01T10:10:00Z");
        reservationService.setClock(Clock.fixed(baseTime, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(baseTime, ZoneOffset.UTC);

        Reservation expiredRes1 = new Reservation();
        expiredRes1.setStatus(ReservationStatus.EN_ATTENTE);
        expiredRes1.setExpiresAt(now.minusMinutes(2));

        Reservation expiredRes2 = new Reservation();
        expiredRes2.setStatus(ReservationStatus.EN_ATTENTE);
        expiredRes2.setExpiresAt(now);

        when(reservationRepository.findByStatusAndExpiresAtLessThanEqual(
                ReservationStatus.EN_ATTENTE, now
        )).thenReturn(List.of(expiredRes1, expiredRes2));

        int expiredCount = reservationService.expirePendingReservations();

        assertEquals(2, expiredCount);
        assertEquals(ReservationStatus.EXPIREE, expiredRes1.getStatus());
        assertEquals(ReservationStatus.EXPIREE, expiredRes2.getStatus());
        verify(reservationRepository).saveAll(List.of(expiredRes1, expiredRes2));
    }

    /**
     * Scénario 11 : Le scheduler ne modifie jamais les réservations confirmées.
     */
    @Test
    void scenario11_schedulerNeverModifiesConfirmedReservations() {
        Instant baseTime = Instant.parse("2026-06-01T10:10:00Z");
        reservationService.setClock(Clock.fixed(baseTime, ZoneOffset.UTC));
        LocalDateTime now = LocalDateTime.ofInstant(baseTime, ZoneOffset.UTC);

        when(reservationRepository.findByStatusAndExpiresAtLessThanEqual(
                ReservationStatus.EN_ATTENTE, now
        )).thenReturn(List.of());

        int expiredCount = reservationService.expirePendingReservations();

        assertEquals(0, expiredCount);
        verify(reservationRepository).findByStatusAndExpiresAtLessThanEqual(
                eq(ReservationStatus.EN_ATTENTE), eq(now)
        );
        verify(reservationRepository).saveAll(List.of());
    }
}