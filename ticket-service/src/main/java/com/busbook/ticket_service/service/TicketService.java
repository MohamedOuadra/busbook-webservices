package com.busbook.ticket_service.service;

import com.busbook.ticket_service.dto.TicketRequest;
import com.busbook.ticket_service.dto.TicketResponse;
import com.busbook.ticket_service.entity.Ticket;
import com.busbook.ticket_service.exception.ResourceNotFoundException;
import com.busbook.ticket_service.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Crée et émet un billet électronique pour une réservation confirmée.
     */
    @Transactional
    public TicketResponse createTicket(TicketRequest request) {

        validateTicketRequest(request);

        // Vérifier si un billet a déjà été généré pour cette réservation
        if (ticketRepository.existsByReservationId(request.getReservationId())) {
            throw new IllegalStateException(
                    "Un billet a déjà été émis pour cette réservation."
            );
        }

        String ticketNumber = generateTicketNumber();
        String qrCode = generateQrCodePayload(ticketNumber, request);

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setReservationId(request.getReservationId());
        ticket.setTripId(request.getTripId());
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setTravelerName(request.getTravelerName());
        ticket.setTravelerEmail(request.getTravelerEmail());
        ticket.setQrCode(qrCode);
        ticket.setIssuedAt(LocalDateTime.now());
        ticket.setValid(true);

        Ticket saved = ticketRepository.save(ticket);

        return TicketResponse.fromEntity(saved);
    }

    /**
     * Récupère un billet par son identifiant.
     */
    public TicketResponse getTicketById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'identifiant du billet doit être positif.");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable."));

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Récupère le billet associé à une réservation.
     */
    public TicketResponse getTicketByReservationId(Long reservationId) {

        if (reservationId == null || reservationId <= 0) {
            throw new IllegalArgumentException("L'identifiant de réservation doit être positif.");
        }

        Ticket ticket = ticketRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun billet trouvé pour cette réservation."));

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Vérifie la validité d'un billet via son numéro unique (ex: lors du scan du QR code).
     */
    public TicketResponse verifyTicket(String ticketNumber) {

        if (ticketNumber == null || ticketNumber.isBlank()) {
            throw new IllegalArgumentException("Le numéro de billet est obligatoire.");
        }

        Ticket ticket = ticketRepository.findByTicketNumberIgnoreCase(ticketNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable avec ce numéro."));

        return TicketResponse.fromEntity(ticket);
    }

    /**
     * Génère un numéro de billet unique de la forme TCK-YYYYMMDD-XXXXXX.
     */
    private String generateTicketNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TCK-" + datePrefix + "-" + randomSuffix;
    }

    /**
     * Génère le payload sécurisé représenté par le QR code.
     */
    private String generateQrCodePayload(String ticketNumber, TicketRequest request) {
        String rawData = String.format(
                "BUSBOOK|%s|RES:%d|TRIP:%d|SEAT:%s",
                ticketNumber,
                request.getReservationId(),
                request.getTripId(),
                request.getSeatNumber()
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 16);
            return rawData + "|SIG:" + signature;
        } catch (NoSuchAlgorithmException e) {
            return rawData;
        }
    }

    /**
     * Valide les données requises pour la création du billet.
     */
    private void validateTicketRequest(TicketRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Les informations du billet sont obligatoires.");
        }

        if (request.getReservationId() == null || request.getReservationId() <= 0) {
            throw new IllegalArgumentException("L'identifiant de réservation doit être positif.");
        }

        if (request.getTripId() == null || request.getTripId() <= 0) {
            throw new IllegalArgumentException("L'identifiant du trajet doit être positif.");
        }

        if (request.getSeatNumber() == null || request.getSeatNumber().isBlank()) {
            throw new IllegalArgumentException("Le numéro de siège est obligatoire.");
        }
    }
}
