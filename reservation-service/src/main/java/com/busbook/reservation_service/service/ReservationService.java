
package com.busbook.reservation_service.service;

import com.busbook.reservation_service.dto.ReservationRequest;
import com.busbook.reservation_service.dto.ReservationResponse;
import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.entity.Seat;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.repository.ReservationRepository;
import com.busbook.reservation_service.repository.SeatRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            SeatRepository seatRepository) {

        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
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

        List<ReservationStatus> activeStatuses = List.of(
                ReservationStatus.EN_ATTENTE,
                ReservationStatus.CONFIRMEE
        );

        return !reservationRepository
                .existsByTripIdAndSeat_IdAndStatusIn(
                        tripId, seatId, activeStatuses
                );
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le siège demandé est introuvable."
                ));

        if (!isSeatAvailable(request.getTripId(), request.getSeatId())) {
            throw new IllegalStateException(
                    "Ce siège est déjà réservé pour ce trajet."
            );
        }

        Reservation reservation = new Reservation();

        reservation.setTripId(request.getTripId());
        reservation.setTravelerId(request.getTravelerId());
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.fromEntity(savedReservation);
    }

    /**
     * Confirme une réservation en attente.
     */
    @Transactional
    public Reservation confirmReservation(Long reservationId) {

        validateReservationId(reservationId);

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Réservation introuvable."
                ));

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE) {
            throw new IllegalStateException(
                    "Seule une réservation en attente peut être confirmée."
            );
        }

        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservation.setConfirmedAt(LocalDateTime.now());

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
                .orElseThrow(() -> new IllegalArgumentException(
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
}
