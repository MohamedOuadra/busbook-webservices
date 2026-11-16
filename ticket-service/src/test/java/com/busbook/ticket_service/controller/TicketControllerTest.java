package com.busbook.ticket_service.controller;

import com.busbook.ticket_service.dto.TicketRequest;
import com.busbook.ticket_service.dto.TicketResponse;
import com.busbook.ticket_service.exception.GlobalExceptionHandler;
import com.busbook.ticket_service.exception.ResourceNotFoundException;
import com.busbook.ticket_service.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TicketResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(ticketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        sampleResponse = new TicketResponse(
                1L,
                "TCK-20261116-A1B2C3D4",
                10L,
                100L,
                "A5",
                "Ahmed Alami",
                "ahmed@example.com",
                "BUSBOOK|TCK-20261116-A1B2C3D4|RES:10|TRIP:100|SEAT:A5",
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void createTicket_ShouldReturn201() throws Exception {
        TicketRequest request = new TicketRequest(10L, 100L, "A5", "Ahmed Alami", "ahmed@example.com");

        when(ticketService.createTicket(any(TicketRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/tickets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("TCK-20261116-A1B2C3D4"))
                .andExpect(jsonPath("$.reservationId").value(10))
                .andExpect(jsonPath("$.tripId").value(100))
                .andExpect(jsonPath("$.seatNumber").value("A5"))
                .andExpect(jsonPath("$.valid").value(true));

        verify(ticketService).createTicket(any(TicketRequest.class));
    }

    @Test
    void createTicket_WhenInvalidRequest_ShouldReturn400() throws Exception {
        TicketRequest request = new TicketRequest(null, null, "");

        mockMvc.perform(post("/api/tickets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createTicket_WhenConflict_ShouldReturn409() throws Exception {
        TicketRequest request = new TicketRequest(10L, 100L, "A5");

        when(ticketService.createTicket(any(TicketRequest.class)))
                .thenThrow(new IllegalStateException("Un billet a déjà été émis pour cette réservation."));

        mockMvc.perform(post("/api/tickets")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Un billet a déjà été émis pour cette réservation."));
    }

    @Test
    void getTicketById_ShouldReturn200() throws Exception {
        when(ticketService.getTicketById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("TCK-20261116-A1B2C3D4"));

        verify(ticketService).getTicketById(1L);
    }

    @Test
    void getTicketById_WhenNotFound_ShouldReturn404() throws Exception {
        when(ticketService.getTicketById(99L))
                .thenThrow(new ResourceNotFoundException("Billet introuvable."));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getTicketByReservationId_ShouldReturn200() throws Exception {
        when(ticketService.getTicketByReservationId(10L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/tickets/reservation/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(10));

        verify(ticketService).getTicketByReservationId(10L);
    }

    @Test
    void verifyTicket_ShouldReturn200() throws Exception {
        when(ticketService.verifyTicket("TCK-20261116-A1B2C3D4")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/tickets/verify/TCK-20261116-A1B2C3D4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("TCK-20261116-A1B2C3D4"))
                .andExpect(jsonPath("$.valid").value(true));

        verify(ticketService).verifyTicket("TCK-20261116-A1B2C3D4");
    }

    @Test
    void verifyTicket_WhenNotFound_ShouldReturn404() throws Exception {
        when(ticketService.verifyTicket("UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Billet introuvable avec ce numéro."));

        mockMvc.perform(get("/api/tickets/verify/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
