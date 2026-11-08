
package com.busbook.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trajets")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String villeDepart;

    @NotBlank
    @Column(nullable = false)
    private String villeArrivee;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateArrivee;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    public Trajet() {
    }

    public Trajet(String villeDepart, String villeArrivee,
                  LocalDateTime dateDepart, LocalDateTime dateArrivee,
                  BigDecimal prix, Bus bus) {
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.prix = prix;
        this.bus = bus;
    }

    public Long getId() {
        return id;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDateTime getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDateTime dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @AssertTrue(message = "La date d'arrivée doit être après la date de départ")
    public boolean isChronologieValide() {
        if (dateDepart == null || dateArrivee == null) {
            return true;
        }

        return dateArrivee.isAfter(dateDepart);
    }

    @AssertTrue(message = "La ville de départ doit être différente de la ville d'arrivée")
    public boolean isVillesDifferentes() {
        if (villeDepart == null || villeArrivee == null) {
            return true;
        }

        return !villeDepart.trim()
                .equalsIgnoreCase(villeArrivee.trim());
    }


}
