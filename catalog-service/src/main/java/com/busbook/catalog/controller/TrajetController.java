package com.busbook.catalog.controller;

import com.busbook.catalog.dto.TrajetRequestDTO;
import com.busbook.catalog.dto.TrajetResponseDTO;
import com.busbook.catalog.service.TrajetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/trips", "/api/trajets"})
@CrossOrigin(origins = "*")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @GetMapping
    public ResponseEntity<List<TrajetResponseDTO>> getAllTrips(
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeArrivee,
            @RequestParam(required = false) String dateDepart
    ) {
        if (villeDepart != null || villeArrivee != null || dateDepart != null) {
            return ResponseEntity.ok(trajetService.searchTrajets(villeDepart, villeArrivee, dateDepart));
        }
        return ResponseEntity.ok(trajetService.getAllTrajets());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrajetResponseDTO>> searchTrips(
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeArrivee,
            @RequestParam(required = false) String dateDepart
    ) {
        return ResponseEntity.ok(trajetService.searchTrajets(villeDepart, villeArrivee, dateDepart));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.getTrajetById(id));
    }

    @PostMapping
    public ResponseEntity<TrajetResponseDTO> createTrip(@Valid @RequestBody TrajetRequestDTO request) {
        TrajetResponseDTO created = trajetService.createTrajet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        trajetService.deleteTrajet(id);
        return ResponseEntity.noContent().build();
    }
}
