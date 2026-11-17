package com.busbook.reservation_service.repository;

import com.busbook.reservation_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    Optional<Seat> findByBusIdAndSeatNumber(Long busId, String seatNumber);
}