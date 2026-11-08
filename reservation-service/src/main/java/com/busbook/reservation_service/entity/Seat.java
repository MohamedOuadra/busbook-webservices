package com.busbook.reservation_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro du siège est obligatoire")
    @Column(nullable = false)
    private String seatNumber;

    @NotNull(message = "L'identifiant du bus est obligatoire")
    @Positive(message = "L'identifiant du bus doit être positif")
    @Column(nullable = false)
    private Long busId;

    public Seat() {
    }

    public Seat(String seatNumber, Long busId) {
        this.seatNumber = seatNumber;
        this.busId = busId;
    }

    public Long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }
}