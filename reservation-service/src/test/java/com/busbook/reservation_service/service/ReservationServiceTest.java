package com.busbook.reservation_service.service;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldReturnTrueWhenSeatIsAvailable() {
        when(reservationRepository.existsByTripIdAndSeat_IdAndStatusIn(
                eq(1L), eq(5L), anyList()
        )).thenReturn(false);

        boolean result = reservationService.isSeatAvailable(1L, 5L);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenSeatIsAlreadyReserved() {
        when(reservationRepository.existsByTripIdAndSeat_IdAndStatusIn(
                eq(1L), eq(5L), anyList()
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

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.confirmReservation(99L));
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
}