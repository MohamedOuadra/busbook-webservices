package com.busbook.reservation_service.service;

import com.busbook.reservation_service.dto.ReservationRequest;
import com.busbook.reservation_service.dto.ReservationResponse;
import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.entity.Seat;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.exception.ResourceNotFoundException;
import com.busbook.reservation_service.repository.ReservationRepository;
import com.busbook.reservation_service.repository.SeatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private static final String SEAT_CONSTRAINT =
            "uk_active_reservation_trip_seat";
    private static final int RESERVATION_EXPIRATION_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private Clock clock;

    @Autowired
    public ReservationService(
            ReservationRepository reservationRepository,
            SeatRepository seatRepository) {
        this(reservationRepository, seatRepository, Clock.systemDefaultZone());
    }

    public ReservationService(
            ReservationRepository reservationRepository,
            SeatRepository seatRepository,
            Clock clock) {

        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
    }

    @Autowired(required = false)
    public void setClock(Clock clock) {
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
    }

    /**
     * Vérifie si un siège est disponible pour un trajet.
     */
    public boolean isSeatAvailable(Long tripId, Long seatId) {

        if (tripId == null || tripId <= 0 ||
                seatId == null || seatId <= 0) {
            throw new IllegalArgumentException(
                    "Les identifiants du trajet et du siège doivent être positifs."
            );
        }

        LocalDateTime now = LocalDateTime.now(clock);

        // Vérifier si seatId correspond à l'identifiant réel du siège
        if (reservationRepository.existsActiveReservation(
                tripId, seatId, now)) {
            return false;
        }

        // Vérifier également si seatId correspond au numéro du siège
        Optional<Seat> seatByNumber =
                seatRepository.findByBusIdAndSeatNumber(
                        tripId, String.valueOf(seatId)
                );

        if (seatByNumber.isPresent()) {
            return !reservationRepository.existsActiveReservation(
                    tripId, seatByNumber.get().getId(), now
            );
        }

        return true;
    }


    /**
     * Crée une nouvelle réservation.
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Les informations de réservation sont obligatoires."
            );
        }

        if (request.getTripId() == null || request.getTripId() <= 0
                || request.getTravelerId() == null || request.getTravelerId() <= 0
                || request.getSeatId() == null || request.getSeatId() <= 0) {

            throw new IllegalArgumentException(
                    "Les identifiants du trajet, du voyageur et du siège doivent être positifs."
            );
        }

        Seat seat = seatRepository.findById(request.getSeatId())
                .or(() -> seatRepository.findByBusIdAndSeatNumber(
                        request.getTripId(),
                        String.valueOf(request.getSeatId())
                ))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Siège introuvable."
                ));

        if (seat.getId() == null || seat.getId() <= 0) {
            throw new ResourceNotFoundException(
                    "Identifiant du siège invalide."
            );
        }

        LocalDateTime now = LocalDateTime.now(clock);

        // Expirer immédiatement les réservations dépassées pour ce siège
        reservationRepository.expirePendingReservationsForSeat(
                request.getTripId(),
                seat.getId(),
                now
        );

        // Vérifier la disponibilité du siège
        if (reservationRepository.existsActiveReservation(
                request.getTripId(),
                seat.getId(),
                now
        )) {
            throw new IllegalStateException(
                    "Ce siège est déjà réservé pour ce trajet."
            );
        }

        Reservation reservation = new Reservation();

        reservation.setTripId(request.getTripId());
        reservation.setTravelerId(request.getTravelerId());
        reservation.setSeat(seat);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        reservation.setExpiresAt(
                now.plusMinutes(RESERVATION_EXPIRATION_MINUTES)
        );

        try {
            Reservation savedReservation =
                    reservationRepository.saveAndFlush(reservation);

            return ReservationResponse.fromEntity(savedReservation);

        } catch (DataIntegrityViolationException exception) {

            if (isSeatConstraintViolation(exception)) {
                throw new IllegalStateException(
                        "Ce siège est déjà réservé pour ce trajet."
                );
            }

            throw exception;
        }
    }

    /**
     * Confirme une réservation en attente.
     */
    @Transactional
    public Reservation confirmReservation(Long reservationId) {

        validateReservationId(reservationId);

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable."
                ));

        LocalDateTime now = LocalDateTime.now(clock);

        if (reservation.getStatus() == ReservationStatus.EN_ATTENTE
                && reservation.getExpiresAt() != null
                && !reservation.getExpiresAt().isAfter(now)) {

            throw new IllegalStateException(
                    "Le délai de confirmation de cette réservation est expiré."
            );
        }

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE) {
            throw new IllegalStateException(
                    "Seule une réservation en attente peut être confirmée."
            );
        }

        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservation.setConfirmedAt(now);

        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation en attente ou confirmée.
     */
    @Transactional
    public Reservation cancelReservation(Long reservationId) {

        validateReservationId(reservationId);

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable."
                ));

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE
                && reservation.getStatus() != ReservationStatus.CONFIRMEE) {

            throw new IllegalStateException(
                    "Cette réservation ne peut pas être annulée."
            );
        }

        reservation.setStatus(ReservationStatus.ANNULEE);

        return reservationRepository.save(reservation);
    }

    /**
     * Vérifie la validité de l'identifiant d'une réservation.
     */
    private void validateReservationId(Long reservationId) {

        if (reservationId == null || reservationId <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de réservation doit être positif."
            );
        }
    }

    /**
     * Expire automatiquement les réservations en attente
     * dont le délai de 10 minutes est dépassé.
     */
    @Transactional
    public int expirePendingReservations() {

        LocalDateTime now = LocalDateTime.now(clock);

        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndExpiresAtLessThanEqual(
                        ReservationStatus.EN_ATTENTE,
                        now
                );

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.EXPIREE);
        }

        reservationRepository.saveAll(expiredReservations);

        return expiredReservations.size();
    }
    /**
     * Récupère une réservation par son identifiant.
     */
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long reservationId) {

        validateReservationId(reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable."
                ));

        return ReservationResponse.fromEntity(reservation);
    }

    /**
     * Récupère toutes les réservations d'un trajet.
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByTripId(Long tripId) {

        if (tripId == null || tripId <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du trajet doit être positif."
            );
        }

        return reservationRepository.findByTripId(tripId)
                .stream()
                .map(ReservationResponse::fromEntity)
                .toList();
    }

    /**
     * Récupère toutes les réservations (pour l'administration).
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::fromEntity)
                .toList();
    }

    /**
     * Récupère toutes les réservations d'un voyageur.
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByTravelerId(Long travelerId) {

        if (travelerId == null || travelerId <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du voyageur doit être positif."
            );
        }

        return reservationRepository.findByTravelerId(travelerId)
                .stream()
                .map(ReservationResponse::fromEntity)
                .toList();
    }

    /**
     * Vérifie si une violation concerne la contrainte
     * d'unicité des réservations actives d'un siège.
     */
    private boolean isSeatConstraintViolation(Throwable exception) {

        Throwable cause = exception;

        while (cause != null) {

            String message = cause.getMessage();

            if (message != null && message.toLowerCase().contains(SEAT_CONSTRAINT.toLowerCase())) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
