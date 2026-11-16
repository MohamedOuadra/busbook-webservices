package com.busbook.ticket_service.controller;

import com.busbook.ticket_service.dto.TicketRequest;
import com.busbook.ticket_service.dto.TicketResponse;
import com.busbook.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Émettre un billet pour une réservation confirmée.
     * POST /api/tickets
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest request) {

        TicketResponse response = ticketService.createTicket(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulter un billet par son ID.
     * GET /api/tickets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable("id") Long id) {

        TicketResponse response = ticketService.getTicketById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Consulter le billet associé à une réservation.
     * GET /api/tickets/reservation/{reservationId}
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<TicketResponse> getTicketByReservationId(
            @PathVariable("reservationId") Long reservationId) {

        TicketResponse response = ticketService.getTicketByReservationId(reservationId);

        return ResponseEntity.ok(response);
    }

    /**
     * Vérifier un billet lors d'un contrôle (scan du QR code).
     * GET /api/tickets/verify/{ticketNumber}
     */
    @GetMapping("/verify/{ticketNumber}")
    public ResponseEntity<TicketResponse> verifyTicket(@PathVariable("ticketNumber") String ticketNumber) {

        TicketResponse response = ticketService.verifyTicket(ticketNumber);

        return ResponseEntity.ok(response);
    }
}
