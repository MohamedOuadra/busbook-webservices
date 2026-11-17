package com.busbook.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BusRequestDTO {

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotNull(message = "La capacité est obligatoire")
    @Positive(message = "La capacité doit être positive")
    private Integer capacite;

    @NotBlank(message = "Le type est obligatoire")
    private String type;

    public BusRequestDTO() {
    }

    public BusRequestDTO(String immatriculation, Integer capacite, String type) {
        this.immatriculation = immatriculation;
        this.capacite = capacite;
        this.type = type;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
