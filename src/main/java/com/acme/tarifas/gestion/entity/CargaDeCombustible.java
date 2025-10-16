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
    private Long id;

    @Column(name = "vehiculo_id", nullable = false)
    private String vehiculoId;

    private LocalDateTime fecha;

    @Column(name = "litros_cargados")
    private Double litrosCargados;

    @Column(name = "kilometros_recorridos")
    private Double kilometrosRecorridos;

    @Column(name = "es_vigente", nullable = false)
    private boolean esVigente = true;
}