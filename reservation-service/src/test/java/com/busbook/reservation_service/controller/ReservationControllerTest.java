package com.busbook.reservation_service.controller;

import com.busbook.reservation_service.dto.ReservationRequest;
import com.busbook.reservation_service.dto.ReservationResponse;
import com.busbook.reservation_service.entity.Reservation;
import com.busbook.reservation_service.enums.ReservationStatus;
import com.busbook.reservation_service.service.ReservationService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .build();

        objectMapper = new ObjectMapper();

        reservationResponse = new ReservationResponse(
                1L,
                10L,
                20L,
                5L,
                "A5",
                ReservationStatus.EN_ATTENTE,
                LocalDateTime.now(),
                null,
                null
        );
    }

    // 1. Tester la création d'une réservation
    @Test
    void createReservation_ShouldReturn201() throws Exception {

        ReservationRequest request = new ReservationRequest();
        request.setTripId(10L);
        request.setTravelerId(20L);
        request.setSeatId(5L);

        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(reservationResponse);

        mockMvc.perform(post("/api/reservations")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.travelerId").value(20))
                .andExpect(jsonPath("$.seatId").value(5))
                .andExpect(jsonPath("$.status").value("EN_ATTENTE"));

        verify(reservationService).createReservation(any(ReservationRequest.class));
    }

    // 2. Tester la récupération d'une réservation par ID
    @Test
    void getReservationById_ShouldReturn200() throws Exception {

        when(reservationService.getReservationById(1L))
                .thenReturn(reservationResponse);

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.status").value("EN_ATTENTE"));

        verify(reservationService).getReservationById(1L);
    }

    // 3. Tester la récupération des réservations par trajet
    @Test
    void getReservationsByTripId_ShouldReturn200() throws Exception {

        when(reservationService.getReservationsByTripId(10L))
                .thenReturn(List.of(reservationResponse));

        mockMvc.perform(get("/api/reservations/trip/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tripId").value(10));

        verify(reservationService).getReservationsByTripId(10L);
    }

    // 4. Tester la récupération des réservations par voyageur
    @Test
    void getReservationsByTravelerId_ShouldReturn200() throws Exception {

        when(reservationService.getReservationsByTravelerId(20L))
                .thenReturn(List.of(reservationResponse));

        mockMvc.perform(get("/api/reservations/traveler/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].travelerId").value(20));

        verify(reservationService).getReservationsByTravelerId(20L);
    }

    // 5. Tester la disponibilité d'un siège
    @Test
    void checkSeatAvailability_ShouldReturn200() throws Exception {

        when(reservationService.isSeatAvailable(10L, 5L))
                .thenReturn(true);

        mockMvc.perform(get("/api/reservations/availability")
                        .param("tripId", "10")
                        .param("seatId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(reservationService).isSeatAvailable(10L, 5L);
    }

    // 6. Tester la confirmation d'une réservation
    @Test
    void confirmReservation_ShouldReturn200() throws Exception {

        Reservation reservation = new Reservation();
        reservation.setTripId(10L);
        reservation.setTravelerId(20L);
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        when(reservationService.confirmReservation(1L))
                .thenReturn(reservation);

        mockMvc.perform(patch("/api/reservations/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.status").value("CONFIRMEE"));

        verify(reservationService).confirmReservation(1L);
    }

    // 7. Tester l'annulation d'une réservation
    @Test
    void cancelReservation_ShouldReturn200() throws Exception {

        Reservation reservation = new Reservation();
        reservation.setTripId(10L);
        reservation.setTravelerId(20L);
        reservation.setStatus(ReservationStatus.ANNULEE);

        when(reservationService.cancelReservation(1L))
                .thenReturn(reservation);

        mockMvc.perform(patch("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.status").value("ANNULEE"));

        verify(reservationService).cancelReservation(1L);
    }
}
