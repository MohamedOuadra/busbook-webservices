package com.busbook.catalog.service;

import com.busbook.catalog.dto.BusRequestDTO;
import com.busbook.catalog.dto.BusResponseDTO;
import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.exception.ResourceNotFoundException;
import com.busbook.catalog.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    private Bus bus;

    @BeforeEach
    void setUp() {
        bus = new Bus("12345-A-6", 40, "Confort");
    }

    @Test
    void shouldReturnAllBuses() {
        when(busRepository.findAll()).thenReturn(List.of(bus));

        List<BusResponseDTO> buses = busService.getAllBuses();

        assertEquals(1, buses.size());
        assertEquals("12345-A-6", buses.get(0).getImmatriculation());
        verify(busRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBusByIdWhenExists() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        BusResponseDTO result = busService.getBusById(1L);

        assertNotNull(result);
        assertEquals("12345-A-6", result.getImmatriculation());
    }

    @Test
    void shouldThrowExceptionWhenBusNotFound() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.getBusById(99L));
    }

    @Test
    void shouldCreateBusSuccessfully() {
        BusRequestDTO request = new BusRequestDTO("99999-Z-6", 45, "VIP");
        when(busRepository.existsByImmatriculation("99999-Z-6")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BusResponseDTO created = busService.createBus(request);

        assertNotNull(created);
        assertEquals("99999-Z-6", created.getImmatriculation());
        assertEquals(45, created.getCapacite());
    }

    @Test
    void shouldRejectBusCreationWhenImmatriculationExists() {
        BusRequestDTO request = new BusRequestDTO("12345-A-6", 45, "VIP");
        when(busRepository.existsByImmatriculation("12345-A-6")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> busService.createBus(request));
        verify(busRepository, never()).save(any(Bus.class));
    }
}
