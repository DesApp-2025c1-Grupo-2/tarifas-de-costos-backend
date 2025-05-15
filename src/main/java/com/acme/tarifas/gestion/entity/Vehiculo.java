package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VehiculoEspecifico")
    private Long id;

    @Column(unique = true)
    private String patenteDominio;

    @ManyToOne
    @JoinColumn(name = "ID_TipoVehiculo")
    private TipoVehiculo tipoVehiculo;

    @ManyToOne
    @JoinColumn(name = "ID_TransportistaPropietario")
    private Transportista transportistaPropietario;
    private String marca;
    private String modelo;
    private Integer anioFabricacion;
    private String descripcionAdicional;
    private LocalDateTime fechaAltaSistema;
    private Boolean activo;

    public Vehiculo() {
    }

    public Vehiculo(Long id, String patenteDominio, TipoVehiculo tipoVehiculo, Transportista transportistaPropietario, String marca, String modelo, Integer anioFabricacion, String descripcionAdicional, LocalDateTime fechaAltaSistema, Boolean activo) {
        this.id = id;
        this.patenteDominio = patenteDominio;
        this.tipoVehiculo = tipoVehiculo;
        this.transportistaPropietario = transportistaPropietario;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.descripcionAdicional = descripcionAdicional;
        this.fechaAltaSistema = fechaAltaSistema;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatenteDominio() {
        return patenteDominio;
    }

    public void setPatenteDominio(String patenteDominio) {
        this.patenteDominio = patenteDominio;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public Transportista getTransportistaPropietario() {
        return transportistaPropietario;
    }

    public void setTransportistaPropietario(Transportista transportistaPropietario) {
        this.transportistaPropietario = transportistaPropietario;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnioFabricacion() {
        return anioFabricacion;
    }

    public void setAnioFabricacion(Integer anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public String getDescripcionAdicional() {
        return descripcionAdicional;
    }

    public void setDescripcionAdicional(String descripcionAdicional) {
        this.descripcionAdicional = descripcionAdicional;
    }

    public LocalDateTime getFechaAltaSistema() {
        return fechaAltaSistema;
    }

    public void setFechaAltaSistema(LocalDateTime fechaAltaSistema) {
        this.fechaAltaSistema = fechaAltaSistema;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}