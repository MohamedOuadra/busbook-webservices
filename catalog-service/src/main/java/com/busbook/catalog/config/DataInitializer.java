package com.busbook.catalog.config;

import com.busbook.catalog.entity.Bus;
import com.busbook.catalog.entity.Trajet;
import com.busbook.catalog.repository.BusRepository;
import com.busbook.catalog.repository.TrajetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initCatalogData(BusRepository busRepository, TrajetRepository trajetRepository) {
        return args -> {
            if (busRepository.count() > 0) {
                return;
            }

            // Create sample fleet
            Bus bus1 = new Bus("12345-A-6", 40, "Confort");
            Bus bus2 = new Bus("67890-B-6", 50, "VIP");
            Bus bus3 = new Bus("11111-C-6", 40, "Standard");
            Bus bus4 = new Bus("22222-D-6", 48, "Executive");

            busRepository.saveAll(List.of(bus1, bus2, bus3, bus4));

            LocalDateTime baseDate = LocalDateTime.of(2026, 11, 20, 8, 0);

            // Create initial Moroccan intercity routes matching frontend expectations
            Trajet t1 = new Trajet(
                    "Agadir",
                    "Casablanca",
                    baseDate,
                    baseDate.plusHours(6),
                    new BigDecimal("120.00"),
                    bus1
            );

            Trajet t2 = new Trajet(
                    "Agadir",
                    "Casablanca",
                    baseDate.plusHours(2).plusMinutes(30),
                    baseDate.plusHours(8).plusMinutes(30),
                    new BigDecimal("150.00"),
                    bus2
            );

            Trajet t3 = new Trajet(
                    "Agadir",
                    "Marrakech",
                    baseDate.plusHours(1),
                    baseDate.plusHours(4).plusMinutes(30),
                    new BigDecimal("90.00"),
                    bus3
            );

            Trajet t4 = new Trajet(
                    "Casablanca",
                    "Marrakech",
                    baseDate.plusHours(6),
                    baseDate.plusHours(9).plusMinutes(30),
                    new BigDecimal("100.00"),
                    bus1
            );

            Trajet t5 = new Trajet(
                    "Marrakech",
                    "Agadir",
                    baseDate.plusHours(10),
                    baseDate.plusHours(13).plusMinutes(30),
                    new BigDecimal("95.00"),
                    bus1
            );

            Trajet t6 = new Trajet(
                    "Casablanca",
                    "Tanger",
                    baseDate.minusMinutes(30),
                    baseDate.plusHours(4),
                    new BigDecimal("140.00"),
                    bus2
            );

            Trajet t7 = new Trajet(
                    "Tanger",
                    "Rabat",
                    baseDate.plusHours(7),
                    baseDate.plusHours(10).plusMinutes(30),
                    new BigDecimal("110.00"),
                    bus3
            );

            trajetRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7));
        };
    }
}
