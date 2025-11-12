package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TiposCargaTarifa")
public class TipoCargaTarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoCargaTarifa")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    private String descripcion;

    @NotNull
    @Column(name = "Activo", nullable = false)
    private boolean activo = true;
}