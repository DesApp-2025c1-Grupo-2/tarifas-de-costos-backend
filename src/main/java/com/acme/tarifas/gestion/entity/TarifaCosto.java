package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TarifasCosto")
public class TarifaCosto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TarifaCosto")
    private Long id;

    @Column(unique = true)
    private String codigoTarifa;

    private String nombreTarifa;

    @ManyToOne
    @JoinColumn(name = "ID_TipoVehiculo")
    private TipoVehiculo tipoVehiculo;

    @ManyToOne
    @JoinColumn(name = "ID_TipoCargaTarifa")
    private TipoCargaTarifa tipoCargaTarifa;

    @ManyToOne
    @JoinColumn(name = "ID_ZonaViaje")
    private ZonaViaje zonaViaje;

    @ManyToOne
    @JoinColumn(name = "ID_Transportista")
    private Transportista transportista;

    private Double valorBase;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;
    private Boolean esVigente;
    private Integer version;

    @OneToMany(mappedBy = "tarifaCosto", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TarifaAdicional> adicionales = new ArrayList<>();

    @Transient
    public Double getValorTotal() {
        if (this.adicionales == null) return this.valorBase;

        return this.valorBase + this.adicionales.stream()
                .mapToDouble(TarifaAdicional::getCostoEspecifico)
                .sum();
    }

    public TarifaCosto() {
    }

    public TarifaCosto(Long id, String codigoTarifa, String nombreTarifa, TipoVehiculo tipoVehiculo, TipoCargaTarifa tipoCargaTarifa, ZonaViaje zonaViaje, Transportista transportista, Double valorBase, LocalDateTime fechaCreacion, LocalDateTime fechaUltimaModificacion, Boolean esVigente, Integer version, List<TarifaAdicional> adicionales) {
        this.id = id;
        this.codigoTarifa = codigoTarifa;
        this.nombreTarifa = nombreTarifa;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoCargaTarifa = tipoCargaTarifa;
        this.zonaViaje = zonaViaje;
        this.transportista = transportista;
        this.valorBase = valorBase;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaModificacion = fechaUltimaModificacion;
        this.esVigente = esVigente;
        this.version = version;
        this.adicionales = adicionales;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoTarifa() {
        return codigoTarifa;
    }

    public void setCodigoTarifa(String codigoTarifa) {
        this.codigoTarifa = codigoTarifa;
    }

    public String getNombreTarifa() {
        return nombreTarifa;
    }

    public void setNombreTarifa(String nombreTarifa) {
        this.nombreTarifa = nombreTarifa;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoCargaTarifa getTipoCargaTarifa() {
        return tipoCargaTarifa;
    }

    public void setTipoCargaTarifa(TipoCargaTarifa tipoCargaTarifa) {
        this.tipoCargaTarifa = tipoCargaTarifa;
    }

    public ZonaViaje getZonaViaje() {
        return zonaViaje;
    }

    public void setZonaViaje(ZonaViaje zonaViaje) {
        this.zonaViaje = zonaViaje;
    }

    public Transportista getTransportista() {
        return transportista;
    }

    public void setTransportista(Transportista transportista) {
        this.transportista = transportista;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public Boolean getEsVigente() {
        return esVigente;
    }

    public void setEsVigente(Boolean esVigente) {
        this.esVigente = esVigente;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<TarifaAdicional> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(List<TarifaAdicional> adicionales) {
        this.adicionales = adicionales;
    }
}