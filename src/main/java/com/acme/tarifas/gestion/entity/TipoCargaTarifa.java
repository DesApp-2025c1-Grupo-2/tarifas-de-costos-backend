package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TiposCargaTarifa")
public class TipoCargaTarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoCargaTarifa")
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(name="Activo")
    private Boolean activo;

    public TipoCargaTarifa() {
    }

    public TipoCargaTarifa(Long id, String nombre, String descripcion,Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
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

    public Boolean getActivo() {return activo;}

    public void setActivo(Boolean activo) {this.activo = activo;}
}