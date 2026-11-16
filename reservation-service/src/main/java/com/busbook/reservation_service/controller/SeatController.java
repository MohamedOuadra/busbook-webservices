package com.busbook.reservation_service.controller;

import com.busbook.reservation_service.entity.Seat;
import com.busbook.reservation_service.exception.ResourceNotFoundException;
import com.busbook.reservation_service.repository.SeatRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expose les sièges disponibles par bus.
 * Utilisé par le frontend Angular pour connaître les IDs réels des sièges en DB.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatRepository seatRepository;

    public SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * Récupère tous les sièges d'un bus.
     * GET /api/seats/bus/{busId}
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<Seat>> getSeatsByBus(@PathVariable("busId") Long busId) {
        List<Seat> seats = seatRepository.findByBusId(busId);
        return ResponseEntity.ok(seats);
    }

    /**
     * Récupère un siège spécifique par busId et numéro.
     * GET /api/seats/bus/{busId}/number/{seatNumber}
     */
    @GetMapping("/bus/{busId}/number/{seatNumber}")
    public ResponseEntity<Seat> getSeatByBusAndNumber(
            @PathVariable("busId") Long busId,
            @PathVariable("seatNumber") String seatNumber) {

        Seat seat = seatRepository.findByBusIdAndSeatNumber(busId, seatNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Siège " + seatNumber + " introuvable pour le bus " + busId
                ));

        return ResponseEntity.ok(seat);
    }
}
