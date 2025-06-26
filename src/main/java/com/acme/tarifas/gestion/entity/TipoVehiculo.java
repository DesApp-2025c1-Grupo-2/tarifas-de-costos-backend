package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TiposVehiculo")
public class TipoVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoVehiculo")
    private Long id;

    @Column(unique = true)
    private String nombre;

    private Float capacidadPesoKG;
    private Float capacidadVolumenM3;
    private String descripcion;

    @Column(name = "Activo")
    private Boolean activo = true;
}
