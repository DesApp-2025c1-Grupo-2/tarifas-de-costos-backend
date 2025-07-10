package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Adicionales")
public class Adicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Adicional")
    private Long id;

    private String nombre;
    private Double costoDefault;
    private String descripcion;

    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

    @Column(name = "EsGlobal", nullable = false)
    private boolean esGlobal = true;
}