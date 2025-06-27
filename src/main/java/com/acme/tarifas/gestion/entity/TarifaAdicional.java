package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tarifas_Adicionales", uniqueConstraints = @UniqueConstraint(columnNames = { "ID_TarifaCosto",
        "ID_Adicional" }))
public class TarifaAdicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Tarifa_Adicional")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TarifaCosto")
    @JsonBackReference
    private TarifaCosto tarifaCosto;

    @ManyToOne
    @JoinColumn(name = "ID_Adicional")
    private Adicional adicional;

    private Double costoEspecifico;

    @Column(name = "Activo")
    private Boolean activo = true;
}
