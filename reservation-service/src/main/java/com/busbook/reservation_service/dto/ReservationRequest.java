package com.busbook.reservation_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequest {

    @NotNull(message = "L'identifiant du trajet est obligatoire")
    @Positive(message = "L'identifiant du trajet doit être positif")
    private Long tripId;

    @NotNull(message = "L'identifiant du voyageur est obligatoire")
    @Positive(message = "L'identifiant du voyageur doit être positif")
    private Long travelerId;

    @NotNull(message = "L'identifiant du siège est obligatoire")
    @Positive(message = "L'identifiant du siège doit être positif")
    private Long seatId;

    public ReservationRequest() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(Long travelerId) {
        this.travelerId = travelerId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }
}
