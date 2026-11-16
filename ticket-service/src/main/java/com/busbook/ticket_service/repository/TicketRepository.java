package com.busbook.ticket_service.repository;

import com.busbook.ticket_service.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByReservationId(Long reservationId);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    boolean existsByReservationId(Long reservationId);
}
