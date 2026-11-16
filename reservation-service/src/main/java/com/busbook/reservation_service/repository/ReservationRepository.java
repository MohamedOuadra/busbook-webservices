package com.busbook.reservation_service.repository;

import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.tripId = :tripId " +
            "AND r.seat.id = :seatId " +
            "AND (" +
            "  r.status = com.busbook.reservation_service.enums.ReservationStatus.CONFIRMEE " +
            "  OR (r.status = com.busbook.reservation_service.enums.ReservationStatus.EN_ATTENTE AND r.expiresAt > :now)" +
            ")")
    boolean existsActiveReservation(
            @Param("tripId") Long tripId,
            @Param("seatId") Long seatId,
            @Param("now") LocalDateTime now
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r " +
            "SET r.status = com.busbook.reservation_service.enums.ReservationStatus.EXPIREE " +
            "WHERE r.tripId = :tripId " +
            "AND r.seat.id = :seatId " +
            "AND r.status = com.busbook.reservation_service.enums.ReservationStatus.EN_ATTENTE " +
            "AND r.expiresAt <= :now")
    int expirePendingReservationsForSeat(
            @Param("tripId") Long tripId,
            @Param("seatId") Long seatId,
            @Param("now") LocalDateTime now
    );

    List<Reservation> findByStatusAndExpiresAtLessThanEqual(
            ReservationStatus status,
            LocalDateTime now
    );
}
