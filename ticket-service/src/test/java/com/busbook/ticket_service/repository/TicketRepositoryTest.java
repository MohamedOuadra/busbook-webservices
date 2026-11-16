package com.busbook.ticket_service.repository;

import com.busbook.ticket_service.entity.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void shouldSaveAndFindTicketById() {
        Ticket ticket = new Ticket(
                "TCK-20261116-001",
                100L,
                10L,
                "B3",
                "Karim Tazi",
                "karim@example.com",
                "BUSBOOK|TCK-20261116-001|RES:100"
        );
        ticket.setIssuedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        assertNotNull(saved.getId());
        Optional<Ticket> found = ticketRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("TCK-20261116-001", found.get().getTicketNumber());
        assertEquals(100L, found.get().getReservationId());
        assertEquals("B3", found.get().getSeatNumber());
    }

    @Test
    void shouldFindByReservationId() {
        Ticket ticket = new Ticket(
                "TCK-20261116-002",
                200L,
                15L,
                "C4",
                "Sara Mansouri",
                "sara@example.com",
                "BUSBOOK|TCK-20261116-002|RES:200"
        );
        ticket.setIssuedAt(LocalDateTime.now());

        ticketRepository.saveAndFlush(ticket);

        Optional<Ticket> found = ticketRepository.findByReservationId(200L);
        assertTrue(found.isPresent());
        assertEquals("TCK-20261116-002", found.get().getTicketNumber());
    }

    @Test
    void shouldFindByTicketNumber() {
        Ticket ticket = new Ticket(
                "TCK-20261116-003",
                300L,
                20L,
                "D1",
                "Omar Idrissi",
                "omar@example.com",
                "BUSBOOK|TCK-20261116-003|RES:300"
        );
        ticket.setIssuedAt(LocalDateTime.now());

        ticketRepository.saveAndFlush(ticket);

        Optional<Ticket> found = ticketRepository.findByTicketNumber("TCK-20261116-003");
        assertTrue(found.isPresent());
        assertEquals(300L, found.get().getReservationId());

        Optional<Ticket> foundIgnoreCase = ticketRepository.findByTicketNumberIgnoreCase("tck-20261116-003");
        assertTrue(foundIgnoreCase.isPresent());
        assertEquals(300L, foundIgnoreCase.get().getReservationId());
    }

    @Test
    void shouldCheckIfExistsByReservationId() {
        Ticket ticket = new Ticket(
                "TCK-20261116-004",
                400L,
                25L,
                "A1",
                "Fatima Zahra",
                "fatima@example.com",
                "BUSBOOK|TCK-20261116-004|RES:400"
        );
        ticket.setIssuedAt(LocalDateTime.now());

        ticketRepository.saveAndFlush(ticket);

        assertTrue(ticketRepository.existsByReservationId(400L));
        assertFalse(ticketRepository.existsByReservationId(999L));
    }
}
