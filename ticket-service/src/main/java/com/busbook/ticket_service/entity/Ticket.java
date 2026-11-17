package com.busbook.ticket_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de billet est obligatoire")
    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @NotNull(message = "L'identifiant de réservation est obligatoire")
    @Positive(message = "L'identifiant de réservation doit être positif")
    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @NotNull(message = "L'identifiant du trajet est obligatoire")
    @Positive(message = "L'identifiant du trajet doit être positif")
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @NotBlank(message = "Le numéro de siège est obligatoire")
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "traveler_name")
    private String travelerName;

    @Column(name = "traveler_email")
    private String travelerEmail;

    @NotBlank(message = "Le code QR est obligatoire")
    @Column(name = "qr_code", nullable = false, columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "is_valid", nullable = false)
    private boolean valid = true;

    public Ticket() {
    }

    public Ticket(
            String ticketNumber,
            Long reservationId,
            Long tripId,
            String seatNumber,
            String travelerName,
            String travelerEmail,
            String qrCode) {

        this.ticketNumber = ticketNumber;
        this.reservationId = reservationId;
        this.tripId = tripId;
        this.seatNumber = seatNumber;
        this.travelerName = travelerName;
        this.travelerEmail = travelerEmail;
        this.qrCode = qrCode;
        this.valid = true;
    }

    @PrePersist
    protected void onCreate() {
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
