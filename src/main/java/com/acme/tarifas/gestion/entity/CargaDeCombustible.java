package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.NotNull; 
import jakarta.validation.constraints.Positive; 

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

    @NotBlank(message = "El ID del vehículo es obligatorio.") 
    @Column(name = "vehiculo_id", nullable = false)
    private String vehiculoId;

    @NotNull(message = "La fecha es obligatoria.") 
    private LocalDateTime fecha;

    @NotNull(message = "Los litros cargados son obligatorios.") 
    @Positive(message = "Los litros deben ser un valor positivo.") 
    @Column(name = "litros_cargados")
    private Double litrosCargados;


    @NotBlank(message = "El número de ticket es obligatorio.")
    @Column(name = "numero_ticket") 
    private String numeroTicket;

    // Agregar:
    @NotNull(message = "El precio total es obligatorio.") 
    @Positive(message = "El precio total debe ser un valor positivo.") 
    @Column(name = "precio_total") 
    private Double precioTotal;

    @Column(name = "es_vigente", nullable = false)
    private boolean esVigente = true;
}