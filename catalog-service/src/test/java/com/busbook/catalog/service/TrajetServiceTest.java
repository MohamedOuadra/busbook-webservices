package com.busbook.catalog.service;

import com.busbook.catalog.dto.BusResponseDTO;
import com.busbook.catalog.dto.TrajetRequestDTO;
import com.busbook.catalog.dto.TrajetResponseDTO;
import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.entity.Trajet;
import com.busbook.catalog.exception.ResourceNotFoundException;
import com.busbook.catalog.repository.BusRepository;
import com.busbook.catalog.repository.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrajetServiceTest {

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusService busService;

    @InjectMocks
    private TrajetService trajetService;

    private Bus bus;
    private Trajet trajet;
    private BusResponseDTO busDTO;

    @BeforeEach
    void setUp() {
        bus = new Bus("12345-A-6", 40, "Confort");
        busDTO = new BusResponseDTO(1L, "12345-A-6", 40, "Confort");

        LocalDateTime depart = LocalDateTime.of(2026, 11, 20, 8, 0);
        LocalDateTime arrivee = depart.plusHours(6);

        trajet = new Trajet("Agadir", "Casablanca", depart, arrivee, new BigDecimal("120.00"), bus);
    }

    @Test
    void shouldReturnAllTrips() {
        when(trajetRepository.findAll()).thenReturn(List.of(trajet));
        when(busService.mapToDTO(any())).thenReturn(busDTO);

        List<TrajetResponseDTO> trips = trajetService.getAllTrajets();

        assertEquals(1, trips.size());
        assertEquals("Agadir", trips.get(0).getVilleDepart());
        assertEquals("Casablanca", trips.get(0).getVilleArrivee());
    }

    @Test
    void shouldReturnTripById() {
        when(trajetRepository.findById(1L)).thenReturn(Optional.of(trajet));
        when(busService.mapToDTO(any())).thenReturn(busDTO);

        TrajetResponseDTO result = trajetService.getTrajetById(1L);

        assertNotNull(result);
        assertEquals("Agadir", result.getVilleDepart());
    }

    @Test
    void shouldThrowExceptionWhenTripNotFound() {
        when(trajetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trajetService.getTrajetById(99L));
    }

    @Test
    void shouldCreateTripSuccessfully() {
        LocalDateTime depart = LocalDateTime.of(2026, 11, 20, 9, 0);
        LocalDateTime arrivee = depart.plusHours(3);

        TrajetRequestDTO request = new TrajetRequestDTO(
                "Agadir", "Marrakech", depart, arrivee, new BigDecimal("90.00"), 1L
        );

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(trajetRepository.save(any(Trajet.class))).thenAnswer(i -> i.getArgument(0));
        when(busService.mapToDTO(any())).thenReturn(busDTO);

        TrajetResponseDTO created = trajetService.createTrajet(request);

        assertNotNull(created);
        assertEquals("Agadir", created.getVilleDepart());
        assertEquals("Marrakech", created.getVilleArrivee());
    }

    @Test
    void shouldRejectTripWhenCitiesAreIdentical() {
        LocalDateTime depart = LocalDateTime.of(2026, 11, 20, 9, 0);
        LocalDateTime arrivee = depart.plusHours(3);

        TrajetRequestDTO request = new TrajetRequestDTO(
                "Agadir", "Agadir", depart, arrivee, new BigDecimal("90.00"), 1L
        );

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        assertThrows(IllegalArgumentException.class, () -> trajetService.createTrajet(request));
    }
}
