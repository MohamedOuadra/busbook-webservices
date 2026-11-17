package com.busbook.catalog.controller;

import com.busbook.catalog.dto.BusResponseDTO;
import com.busbook.catalog.dto.TrajetResponseDTO;
import com.busbook.catalog.service.TrajetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrajetControllerTest {

    @Mock
    private TrajetService trajetService;

    @InjectMocks
    private TrajetController trajetController;

    @Test
    void shouldReturnAllTrips() {
        BusResponseDTO busDTO = new BusResponseDTO(1L, "12345-A-6", 40, "Confort");
        TrajetResponseDTO trip = new TrajetResponseDTO(
                1L, "Agadir", "Casablanca",
                LocalDateTime.now(), LocalDateTime.now().plusHours(6),
                new BigDecimal("120.00"), busDTO
        );

        when(trajetService.getAllTrajets()).thenReturn(List.of(trip));

        ResponseEntity<List<TrajetResponseDTO>> response = trajetController.getAllTrips(null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Agadir", response.getBody().get(0).getVilleDepart());
    }

    @Test
    void shouldReturnTripById() {
        BusResponseDTO busDTO = new BusResponseDTO(1L, "12345-A-6", 40, "Confort");
        TrajetResponseDTO trip = new TrajetResponseDTO(
                1L, "Agadir", "Casablanca",
                LocalDateTime.now(), LocalDateTime.now().plusHours(6),
                new BigDecimal("120.00"), busDTO
        );

        when(trajetService.getTrajetById(1L)).thenReturn(trip);

        ResponseEntity<TrajetResponseDTO> response = trajetController.getTripById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }
}
