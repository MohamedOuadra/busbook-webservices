package com.busbook.reservation_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationExpirationSchedulerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationExpirationScheduler scheduler;

    @Test
    void shouldCallExpirePendingReservationsOnScheduledTrigger() {
        when(reservationService.expirePendingReservations()).thenReturn(2);

        scheduler.expireReservations();

        verify(reservationService).expirePendingReservations();
    }

    @Test
    void shouldHandleZeroExpiredReservationsGracefully() {
        when(reservationService.expirePendingReservations()).thenReturn(0);

        scheduler.expireReservations();

        verify(reservationService).expirePendingReservations();
    }
}
