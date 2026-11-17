
package com.busbook.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(
        name = "buses",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "immatriculation"
        )
)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(nullable = false)
    private String immatriculation;

    @NotNull(message = "La capacité est obligatoire")
    @Positive(message = "La capacité doit être positive")
    @Column(nullable = false)
    private Integer capacite;

    @NotBlank(message = "Le type est obligatoire")
    @Column(nullable = false)
    private String type;

    public Bus() {
    }

    public Bus(String immatriculation, Integer capacite, String type) {
        this.immatriculation = immatriculation;
        this.capacite = capacite;
        this.type = type;
    }

    public Long getId() {
        return id;
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
