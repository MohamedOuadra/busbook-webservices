package com.busbook.ticket_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TicketRequest {

    @NotNull(message = "L'identifiant de réservation est obligatoire")
    @Positive(message = "L'identifiant de réservation doit être positif")
    private Long reservationId;

    @NotNull(message = "L'identifiant du trajet est obligatoire")
    @Positive(message = "L'identifiant du trajet doit être positif")
    private Long tripId;

    @NotBlank(message = "Le numéro de siège est obligatoire")
    private String seatNumber;

    private String travelerName;

    private String travelerEmail;

    public TicketRequest() {
    }

    public TicketRequest(Long reservationId, Long tripId, String seatNumber) {
        this(reservationId, tripId, seatNumber, null, null);
    }

    public TicketRequest(Long reservationId, Long tripId, String seatNumber, String travelerName, String travelerEmail) {
        this.reservationId = reservationId;
        this.tripId = tripId;
        this.seatNumber = seatNumber;
        this.travelerName = travelerName;
        this.travelerEmail = travelerEmail;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTravelerName() {
        return travelerName;
    }

    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }

    public String getTravelerEmail() {
        return travelerEmail;
    }

    public void setTravelerEmail(String travelerEmail) {
        this.travelerEmail = travelerEmail;
    }
}
