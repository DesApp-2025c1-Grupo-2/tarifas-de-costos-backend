package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CargasDeCombustible")
public class CargaDeCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CargaCombustible")
    private Long id;

    @Column(name = "ID_Vehiculo", nullable = false)
    private String vehiculoId; // ID externo del vehículo

    private LocalDateTime fecha;
    private Integer cantidadTanques;
    private Double precioPorLitro;
    private Double costoTotal;

    @Column(name = "EsVigente", nullable = false)
    private boolean esVigente = true;
}