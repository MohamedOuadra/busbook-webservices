
package com.busbook.catalog.repository;

import com.busbook.catalog.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByVilleDepartAndVilleArrivee(
            String villeDepart,
            String villeArrivee
    );
}
