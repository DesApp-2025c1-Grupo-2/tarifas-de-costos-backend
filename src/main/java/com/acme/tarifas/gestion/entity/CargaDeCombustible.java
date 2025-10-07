package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CargasDeCombustible")
public class CargaDeCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehiculoId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer cantidadTanques; // Nuevo campo

    @Column(nullable = false)
    private Double precioPorLitro; // Nuevo campo

    @Column(nullable = false)
    private Double costoTotal;

    @Column(nullable = false)
    private boolean esVigente = true;
}