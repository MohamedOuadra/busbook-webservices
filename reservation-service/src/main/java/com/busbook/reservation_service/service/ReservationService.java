package com.busbook.reservation_service.service;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
     * Confirme une réservation en attente.
     */
    public Reservation confirmReservation(Long reservationId) {

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
    public Reservation cancelReservation(Long reservationId) {

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
}
