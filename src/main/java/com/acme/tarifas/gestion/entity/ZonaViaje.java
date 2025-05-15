package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

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


    public ZonaViaje() {
    }

    public ZonaViaje(Long id, String nombre, String descripcion, String regionMapa) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.regionMapa = regionMapa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRegionMapa() {
        return regionMapa;
    }

    public void setRegionMapa(String regionMapa) {
        this.regionMapa = regionMapa;
    }
}