package com.busbook.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrajetResponseDTO {

    private Long id;
    private String villeDepart;
    private String villeArrivee;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArrivee;
    private BigDecimal prix;
    private BusResponseDTO bus;

    public TrajetResponseDTO() {
    }

    public TrajetResponseDTO(Long id, String villeDepart, String villeArrivee,
                             LocalDateTime dateDepart, LocalDateTime dateArrivee,
                             BigDecimal prix, BusResponseDTO bus) {
        this.id = id;
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

    public void setId(Long id) {
        this.id = id;
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

    public BusResponseDTO getBus() {
        return bus;
    }

    public void setBus(BusResponseDTO bus) {
        this.bus = bus;
    }
}
