package com.busbook.reservation_service.repository;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTripId(Long tripId);

    List<Reservation> findByTravelerId(Long travelerId);

    List<Reservation> findByTripIdAndStatus(
            Long tripId,
            ReservationStatus status
    );

    boolean existsByTripIdAndSeat_IdAndStatusIn(
            Long tripId,
            Long seatId,
            List<ReservationStatus> statuses
    );
}