package com.busbook.ticket_service.dto;

import com.busbook.ticket_service.entity.Ticket;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private Long reservationId;
    private Long tripId;
    private String seatNumber;
    private String travelerName;
    private String travelerEmail;
    private String qrCode;
    private LocalDateTime issuedAt;
    private boolean valid;

    public TicketResponse() {
    }

    public TicketResponse(
            Long id,
            String ticketNumber,
            Long reservationId,
            Long tripId,
            String seatNumber,
            String travelerName,
            String travelerEmail,
            String qrCode,
            LocalDateTime issuedAt,
            boolean valid) {

        this.id = id;
        this.ticketNumber = ticketNumber;
        this.reservationId = reservationId;
        this.tripId = tripId;
        this.seatNumber = seatNumber;
        this.travelerName = travelerName;
        this.travelerEmail = travelerEmail;
        this.qrCode = qrCode;
        this.issuedAt = issuedAt;
        this.valid = valid;
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        return new TicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getReservationId(),
                ticket.getTripId(),
                ticket.getSeatNumber(),
                ticket.getTravelerName(),
                ticket.getTravelerEmail(),
                ticket.getQrCode(),
                ticket.getIssuedAt(),
                ticket.isValid()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getTripId() {
        return tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getTravelerName() {
        return travelerName;
    }

    public String getTravelerEmail() {
        return travelerEmail;
    }

    public String getQrCode() {
        return qrCode;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isValid() {
        return valid;
    }
}
