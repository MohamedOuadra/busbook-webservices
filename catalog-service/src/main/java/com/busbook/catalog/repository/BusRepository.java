
package com.busbook.catalog.repository;

import com.busbook.catalog.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByImmatriculation(String immatriculation);

    Optional<Bus> findByImmatriculation(String immatriculation);
}
