package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TarifasCostoHistorial")
public class TarifaCostoHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Historial")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TarifaCostoOriginal")
    private TarifaCosto tarifaOriginal;

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
    private LocalDateTime fechaModificacion;

    @Column(name = "ID_UsuarioModifico")
    private Integer idUsuarioModifico;

    private String comentarioCambio;

    public TarifaCostoHistorial() {
    }

    public TarifaCostoHistorial(Long id, TarifaCosto tarifaOriginal, String codigoTarifa, String nombreTarifa, TipoVehiculo tipoVehiculo, TipoCargaTarifa tipoCargaTarifa, ZonaViaje zonaViaje, Transportista transportista, Double valorBase, LocalDateTime fechaModificacion, Integer idUsuarioModifico, String comentarioCambio) {
        this.id = id;
        this.tarifaOriginal = tarifaOriginal;
        this.codigoTarifa = codigoTarifa;
        this.nombreTarifa = nombreTarifa;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoCargaTarifa = tipoCargaTarifa;
        this.zonaViaje = zonaViaje;
        this.transportista = transportista;
        this.valorBase = valorBase;
        this.fechaModificacion = fechaModificacion;
        this.idUsuarioModifico = idUsuarioModifico;
        this.comentarioCambio = comentarioCambio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TarifaCosto getTarifaOriginal() {
        return tarifaOriginal;
    }

    public void setTarifaOriginal(TarifaCosto tarifaOriginal) {
        this.tarifaOriginal = tarifaOriginal;
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

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getIdUsuarioModifico() {
        return idUsuarioModifico;
    }

    public void setIdUsuarioModifico(Integer idUsuarioModifico) {
        this.idUsuarioModifico = idUsuarioModifico;
    }

    public String getComentarioCambio() {
        return comentarioCambio;
    }

    public void setComentarioCambio(String comentarioCambio) {
        this.comentarioCambio = comentarioCambio;
    }
}