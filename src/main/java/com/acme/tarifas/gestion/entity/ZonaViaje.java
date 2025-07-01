package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ZonasViaje")
public class ZonaViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ZonaViaje")
    private Long id;

    private String nombre;
    private String descripcion;
    private String regionMapa;

    @Column(name = "Activo")
    private Boolean activo = true;

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo);
    }


}
