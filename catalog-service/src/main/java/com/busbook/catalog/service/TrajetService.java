package com.busbook.catalog.service;

import com.busbook.catalog.dto.BusResponseDTO;
import com.busbook.catalog.dto.TrajetRequestDTO;
import com.busbook.catalog.dto.TrajetResponseDTO;
import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.entity.Trajet;
import com.busbook.catalog.exception.ResourceNotFoundException;
import com.busbook.catalog.repository.BusRepository;
import com.busbook.catalog.repository.TrajetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final BusRepository busRepository;
    private final BusService busService;

    public TrajetService(TrajetRepository trajetRepository,
                         BusRepository busRepository,
                         BusService busService) {
        this.trajetRepository = trajetRepository;
        this.busRepository = busRepository;
        this.busService = busService;
    }

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> getAllTrajets() {
        return trajetRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrajetResponseDTO getTrajetById(Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet non trouvé avec l'id : " + id));
        return mapToDTO(trajet);
    }

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> searchTrajets(String villeDepart, String villeArrivee, String dateStr) {
        String dep = (villeDepart != null && !villeDepart.trim().isEmpty()) ? villeDepart.trim() : null;
        String arr = (villeArrivee != null && !villeArrivee.trim().isEmpty()) ? villeArrivee.trim() : null;

        LocalDateTime dateStart = null;
        LocalDateTime dateEnd = null;

        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                // Accepts YYYY-MM-DD or full ISO
                LocalDate parsedDate = LocalDate.parse(dateStr.trim().substring(0, Math.min(10, dateStr.trim().length())));
                dateStart = parsedDate.atStartOfDay();
                dateEnd = parsedDate.atTime(23, 59, 59);
            } catch (DateTimeParseException ignored) {
                // If parsing fails, don't filter strictly by date
            }
        }

        return trajetRepository.searchTrajets(dep, arr, dateStart, dateEnd)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TrajetResponseDTO createTrajet(TrajetRequestDTO request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bus non trouvé avec l'id : " + request.getBusId()
                ));

        Trajet trajet = new Trajet(
                request.getVilleDepart().trim(),
                request.getVilleArrivee().trim(),
                request.getDateDepart(),
                request.getDateArrivee(),
                request.getPrix(),
                bus
        );

        if (!trajet.isChronologieValide()) {
            throw new IllegalArgumentException("La date d'arrivée doit être après la date de départ");
        }

        if (!trajet.isVillesDifferentes()) {
            throw new IllegalArgumentException("La ville de départ doit être différente de la ville d'arrivée");
        }

        Trajet saved = trajetRepository.save(trajet);
        return mapToDTO(saved);
    }

    public void deleteTrajet(Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trajet non trouvé avec l'id : " + id);
        }
        trajetRepository.deleteById(id);
    }

    public TrajetResponseDTO mapToDTO(Trajet trajet) {
        if (trajet == null) return null;

        BusResponseDTO busDTO = busService.mapToDTO(trajet.getBus());

        return new TrajetResponseDTO(
                trajet.getId(),
                trajet.getVilleDepart(),
                trajet.getVilleArrivee(),
                trajet.getDateDepart(),
                trajet.getDateArrivee(),
                trajet.getPrix(),
                busDTO
        );
    }
}
