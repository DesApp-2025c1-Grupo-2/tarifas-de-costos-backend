package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
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

    private String nombre;
    private String descripcion;

    @Column(name = "Activo")
    private Boolean activo = true;
}
