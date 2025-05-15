package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

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

    public TipoVehiculo() {
    }

    public TipoVehiculo(Long id, String nombre, Float capacidadPesoKG, Float capacidadVolumenM3, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadPesoKG = capacidadPesoKG;
        this.capacidadVolumenM3 = capacidadVolumenM3;
        this.descripcion = descripcion;
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

    public Float getCapacidadPesoKG() {
        return capacidadPesoKG;
    }

    public void setCapacidadPesoKG(Float capacidadPesoKG) {
        this.capacidadPesoKG = capacidadPesoKG;
    }

    public Float getCapacidadVolumenM3() {
        return capacidadVolumenM3;
    }

    public void setCapacidadVolumenM3(Float capacidadVolumenM3) {
        this.capacidadVolumenM3 = capacidadVolumenM3;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}