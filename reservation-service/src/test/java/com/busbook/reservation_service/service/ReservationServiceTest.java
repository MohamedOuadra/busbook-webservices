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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        when(reservationRepository.existsByTripIdAndSeat_IdAndStatusIn(
                eq(1L), eq(12L), anyList()
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

        when(seatRepository.findById(12L))
                .thenReturn(Optional.of(seat));

        when(reservationRepository.existsByTripIdAndSeat_IdAndStatusIn(
                eq(1L), eq(12L), anyList()
        )).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> reservationService.createReservation(request));

        verify(reservationRepository, never()).save(any());
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
}