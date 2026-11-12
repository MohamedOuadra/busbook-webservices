package com.busbook.reservation_service.controller;

import com.busbook.reservation_service.dto.ReservationRequest;
import com.busbook.reservation_service.dto.ReservationResponse;
import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Créer une nouvelle réservation.
     * POST /api/reservations
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response =
                reservationService.createReservation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une réservation par son identifiant.
     * GET /api/reservations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id) {

        ReservationResponse response =
                reservationService.getReservationById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer les réservations d'un trajet.
     * GET /api/reservations/trip/{tripId}
     */
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByTripId(
            @PathVariable Long tripId) {

        List<ReservationResponse> reservations =
                reservationService.getReservationsByTripId(tripId);

        return ResponseEntity.ok(reservations);
    }

    /**
     * Récupérer les réservations d'un voyageur.
     * GET /api/reservations/traveler/{travelerId}
     */
    @GetMapping("/traveler/{travelerId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByTravelerId(
            @PathVariable Long travelerId) {

        List<ReservationResponse> reservations =
                reservationService.getReservationsByTravelerId(travelerId);

        return ResponseEntity.ok(reservations);
    }

    /**
     * Vérifier la disponibilité d'un siège pour un trajet.
     * GET /api/reservations/availability?tripId=1&seatId=5
     */
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkSeatAvailability(
            @RequestParam Long tripId,
            @RequestParam Long seatId) {

        boolean available =
                reservationService.isSeatAvailable(tripId, seatId);

        return ResponseEntity.ok(available);
    }

    /**
     * Confirmer une réservation.
     * PATCH /api/reservations/{id}/confirm
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(
            @PathVariable Long id) {

        Reservation reservation =
                reservationService.confirmReservation(id);

        return ResponseEntity.ok(
                ReservationResponse.fromEntity(reservation)
        );
    }

    /**
     * Annuler une réservation.
     * PATCH /api/reservations/{id}/cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id) {

        Reservation reservation =
                reservationService.cancelReservation(id);

        return ResponseEntity.ok(
                ReservationResponse.fromEntity(reservation)
        );
    }
}
