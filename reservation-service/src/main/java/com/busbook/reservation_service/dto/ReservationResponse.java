package com.busbook.reservation_service.dto;

import java.time.LocalDateTime;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;

public class ReservationResponse {

    private Long id;
    private Long tripId;
    private Long travelerId;
    private Long seatId;
    private String seatNumber;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    public ReservationResponse() {
    }

    public ReservationResponse(
            Long id,
            Long tripId,
            Long travelerId,
            Long seatId,
            String seatNumber,
            ReservationStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime confirmedAt) {

        this.id = id;
        this.tripId = tripId;
        this.travelerId = travelerId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
    }

    public static ReservationResponse fromEntity(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getTripId(),
                reservation.getTravelerId(),
                reservation.getSeat() != null ? reservation.getSeat().getId() : null,
                reservation.getSeat() != null ? reservation.getSeat().getSeatNumber() : null,
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiresAt(),
                reservation.getConfirmedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public Long getTravelerId() {
        return travelerId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
}
