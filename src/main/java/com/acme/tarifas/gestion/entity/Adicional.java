package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Adicionales")
public class Adicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Adicional")
    private Long id;

    private String nombre;
    private Double costoDefault;
    private String descripcion;

    public Adicional() {
    }

    public Adicional(Long id, String nombre, Double costoDefault, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.costoDefault = costoDefault;
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

    public Double getCostoDefault() {
        return costoDefault;
    }

    public void setCostoDefault(Double costoDefault) {
        this.costoDefault = costoDefault;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}