package com.busbook.reservation_service.config;

import com.busbook.reservation_service.entity.Seat;
import com.busbook.reservation_service.repository.SeatRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialise la table 'seats' au démarrage si elle est vide.
 * Crée 40 sièges pour chacun des 3 bus utilisés par les trajets mock du frontend.
 * Bus 1 : Agadir → Casablanca (trip mock id=1)
 * Bus 2 : Agadir → Marrakech  (trip mock id=2)
 * Bus 3 : Réserve               (trip mock id=3)
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final SeatRepository seatRepository;

    public DataSeeder(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {
        if (seatRepository.count() > 0) {
            System.out.println("[DataSeeder] Sièges déjà présents en base, initialisation ignorée.");
            return;
        }

        // Bus 1 : Confort  40 sièges  (trip id=1)
        // Bus 2 : VIP      50 sièges  (trip id=2)
        // Bus 3 : Standard 40 sièges  (trip id=3)
        int[] capaciteParBus = { 40, 50, 40 };

        int total = 0;
        for (int i = 0; i < capaciteParBus.length; i++) {
            long busId = i + 1;
            int capacite = capaciteParBus[i];
            for (int numero = 1; numero <= capacite; numero++) {
                seatRepository.save(new Seat(String.valueOf(numero), busId));
            }
            total += capacite;
        }

        System.out.printf("[DataSeeder] %d sièges créés (bus 1=40, bus 2=50, bus 3=40).%n", total);
    }
}
