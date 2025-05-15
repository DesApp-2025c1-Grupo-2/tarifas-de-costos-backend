package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TiposCargaEspecifica")
public class TipoCargaEspecifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoCargaEspecifica")
    private Long id;

    private String nombre;
    private Boolean requiereManipulacionEspecial;
    private String descripcion;


    public TipoCargaEspecifica() {
    }

    public TipoCargaEspecifica(Long id, String nombre, Boolean requiereManipulacionEspecial, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.requiereManipulacionEspecial = requiereManipulacionEspecial;
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

    public Boolean getRequiereManipulacionEspecial() {
        return requiereManipulacionEspecial;
    }

    public void setRequiereManipulacionEspecial(Boolean requiereManipulacionEspecial) {
        this.requiereManipulacionEspecial = requiereManipulacionEspecial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}