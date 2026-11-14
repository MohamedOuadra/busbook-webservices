package com.busbook.reservation_service.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    public ReservationExpirationScheduler(
            ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Vérifie les réservations expirées toutes les 30 secondes.
     */
    @Scheduled(fixedDelay = 30_000)
    public void expireReservations() {
        int expiredCount =
                reservationService.expirePendingReservations();

        if (expiredCount > 0) {
            System.out.println(
                    expiredCount + " réservation(s) expirée(s)."
            );
        }
    }
}
