package com.busbook.catalog.dto;

public class BusResponseDTO {

    private Long id;
    private String immatriculation;
    private Integer capacite;
    private String type;

    public BusResponseDTO() {
    }

    public BusResponseDTO(Long id, String immatriculation, Integer capacite, String type) {
        this.id = id;
        this.immatriculation = immatriculation;
        this.capacite = capacite;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
