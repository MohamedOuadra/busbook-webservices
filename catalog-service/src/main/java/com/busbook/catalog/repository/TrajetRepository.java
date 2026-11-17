package com.busbook.catalog.repository;

import com.busbook.catalog.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByVilleDepartAndVilleArrivee(
            String villeDepart,
            String villeArrivee
    );

    List<Trajet> findByVilleDepartIgnoreCaseAndVilleArriveeIgnoreCase(
            String villeDepart,
            String villeArrivee
    );

    List<Trajet> findByVilleDepartIgnoreCase(String villeDepart);

    List<Trajet> findByVilleArriveeIgnoreCase(String villeArrivee);

    @Query("SELECT t FROM Trajet t WHERE " +
            "(:villeDepart IS NULL OR LOWER(t.villeDepart) = LOWER(:villeDepart)) AND " +
            "(:villeArrivee IS NULL OR LOWER(t.villeArrivee) = LOWER(:villeArrivee)) AND " +
            "(CAST(:dateStart AS java.time.LocalDateTime) IS NULL OR t.dateDepart >= :dateStart) AND " +
            "(CAST(:dateEnd AS java.time.LocalDateTime) IS NULL OR t.dateDepart <= :dateEnd)")
    List<Trajet> searchTrajets(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrivee") String villeArrivee,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd
    );
}
