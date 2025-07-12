package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;


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

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser positivo")
    private Double costoDefault;

    @Size(max=75, message = "'descripcion' No puede tener mas de 75 caracteres")
    private String descripcion;

    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

    @Column(name = "EsGlobal", nullable = false)
    private boolean esGlobal = true;
}