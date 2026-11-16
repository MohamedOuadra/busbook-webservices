package com.busbook.ticket_service.service;

import com.busbook.ticket_service.dto.TicketRequest;
import com.busbook.ticket_service.dto.TicketResponse;
import com.busbook.ticket_service.entity.Ticket;
import com.busbook.ticket_service.exception.ResourceNotFoundException;
import com.busbook.ticket_service.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldCreateTicketSuccessfully() {
        TicketRequest request = new TicketRequest(
                1L, 10L, "A5", "Yassine Berrada", "yassine@example.com"
        );

        when(ticketRepository.existsByReservationId(1L)).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            ReflectionTestUtils.setField(t, "id", 100L);
            return t;
        });

        TicketResponse response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getReservationId());
        assertEquals(10L, response.getTripId());
        assertEquals("A5", response.getSeatNumber());
        assertEquals("Yassine Berrada", response.getTravelerName());
        assertNotNull(response.getTicketNumber());
        assertTrue(response.getTicketNumber().startsWith("TCK-"));
        assertNotNull(response.getQrCode());
        assertTrue(response.getQrCode().contains("BUSBOOK"));
        assertTrue(response.isValid());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldRejectTicketCreationWhenAlreadyExistsForReservation() {
        TicketRequest request = new TicketRequest(1L, 10L, "A5");

        when(ticketRepository.existsByReservationId(1L)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> ticketService.createTicket(request)
        );

        assertEquals("Un billet a déjà été émis pour cette réservation.", ex.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldRejectNullTicketRequest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(null)
        );
    }

    @Test
    void shouldRejectInvalidReservationId() {
        TicketRequest request = new TicketRequest(0L, 10L, "A5");

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );
    }

    @Test
    void shouldRejectInvalidTripId() {
        TicketRequest request = new TicketRequest(1L, -1L, "A5");

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );
    }

    @Test
    void shouldRejectBlankSeatNumber() {
        TicketRequest request = new TicketRequest(1L, 10L, "   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );
    }

    @Test
    void shouldGetTicketById() {
        Ticket ticket = new Ticket("TCK-1", 1L, 10L, "A5", "Ahmed", "ahmed@example.com", "QR1");
        ReflectionTestUtils.setField(ticket, "id", 50L);
        ticket.setIssuedAt(LocalDateTime.now());

        when(ticketRepository.findById(50L)).thenReturn(Optional.of(ticket));

        TicketResponse response = ticketService.getTicketById(50L);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals("TCK-1", response.getTicketNumber());
    }

    @Test
    void shouldThrowWhenTicketNotFoundById() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.getTicketById(999L)
        );
    }

    @Test
    void shouldGetTicketByReservationId() {
        Ticket ticket = new Ticket("TCK-2", 25L, 10L, "B1", "Samir", "samir@example.com", "QR2");
        ReflectionTestUtils.setField(ticket, "id", 60L);

        when(ticketRepository.findByReservationId(25L)).thenReturn(Optional.of(ticket));

        TicketResponse response = ticketService.getTicketByReservationId(25L);

        assertNotNull(response);
        assertEquals(25L, response.getReservationId());
        assertEquals("TCK-2", response.getTicketNumber());
    }

    @Test
    void shouldThrowWhenTicketNotFoundByReservationId() {
        when(ticketRepository.findByReservationId(888L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.getTicketByReservationId(888L)
        );
    }

    @Test
    void shouldVerifyTicketByNumber() {
        Ticket ticket = new Ticket("TCK-VERIFY-001", 30L, 10L, "C3", "Nadia", "nadia@example.com", "QR3");
        ReflectionTestUtils.setField(ticket, "id", 70L);

        when(ticketRepository.findByTicketNumberIgnoreCase("TCK-VERIFY-001")).thenReturn(Optional.of(ticket));

        TicketResponse response = ticketService.verifyTicket("TCK-VERIFY-001");

        assertNotNull(response);
        assertEquals("TCK-VERIFY-001", response.getTicketNumber());
        assertTrue(response.isValid());
    }

    @Test
    void shouldThrowWhenVerifyingNonExistentTicket() {
        when(ticketRepository.findByTicketNumberIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.verifyTicket("UNKNOWN")
        );
    }
}
