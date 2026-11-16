package com.busbook.payment_service.client;

public interface ReservationClient {

    boolean confirmReservation(Long reservationId);

    boolean cancelReservation(Long reservationId);
}
