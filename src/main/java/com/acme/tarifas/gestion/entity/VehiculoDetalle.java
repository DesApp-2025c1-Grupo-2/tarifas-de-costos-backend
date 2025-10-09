package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "VehiculoDetalles")
public class VehiculoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vehiculoId; // ID del vehículo de la API externa

    @Column(nullable = false)
    private Double litrosPorTanque;

    @Column(nullable = false)
    private Double kmPorTanque; // Kilómetros que rinde un tanque lleno

    private String tipoCombustible; // Ej: Diesel, Nafta, etc.
}