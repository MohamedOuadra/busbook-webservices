package com.busbook.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrajetRequestDTO {

    @NotBlank(message = "La ville de départ est obligatoire")
    private String villeDepart;

    @NotBlank(message = "La ville d'arrivée est obligatoire")
    private String villeArrivee;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDateTime dateDepart;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDateTime dateArrivee;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à zéro")
    private BigDecimal prix;

    @NotNull(message = "L'identifiant du bus est obligatoire")
    @Positive(message = "L'identifiant du bus doit être positif")
    private Long busId;

    public TrajetRequestDTO() {
    }

    public TrajetRequestDTO(String villeDepart, String villeArrivee,
                            LocalDateTime dateDepart, LocalDateTime dateArrivee,
                            BigDecimal prix, Long busId) {
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.prix = prix;
        this.busId = busId;
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

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }
}
