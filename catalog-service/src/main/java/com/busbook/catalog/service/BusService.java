package com.busbook.catalog.service;

import com.busbook.catalog.dto.BusRequestDTO;
import com.busbook.catalog.dto.BusResponseDTO;
import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.exception.ResourceNotFoundException;
import com.busbook.catalog.repository.BusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusService {

    private final BusRepository busRepository;

    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Transactional(readOnly = true)
    public List<BusResponseDTO> getAllBuses() {
        return busRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusResponseDTO getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus non trouvé avec l'id : " + id));
        return mapToDTO(bus);
    }

    public BusResponseDTO createBus(BusRequestDTO request) {
        if (busRepository.existsByImmatriculation(request.getImmatriculation())) {
            throw new IllegalArgumentException(
                    "Un bus avec l'immatriculation " + request.getImmatriculation() + " existe déjà."
            );
        }

        Bus bus = new Bus(
                request.getImmatriculation(),
                request.getCapacite(),
                request.getType()
        );

        Bus saved = busRepository.save(bus);
        return mapToDTO(saved);
    }

    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bus non trouvé avec l'id : " + id);
        }
        busRepository.deleteById(id);
    }

    public BusResponseDTO mapToDTO(Bus bus) {
        if (bus == null) return null;
        return new BusResponseDTO(
                bus.getId(),
                bus.getImmatriculation(),
                bus.getCapacite(),
                bus.getType()
        );
    }
}
