package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import java.util.List;

public class TarifaCostoDTO {

    private Long id;
    private String nombre;
    private Double valorBase;
    private boolean esVigente; // Corregido a primitivo boolean
    private String transportistaNombre;
    private String tipoVehiculoNombre;
    private String zonaNombre;
    private String tipoCargaNombre;
    private String transportistaId; //Ahora es string porque viajes usa mongoid
    private Long tipoVehiculoId;
    private Long zonaId;
    private Long tipoCargaId;
    private Double total;
    private List<TarifaAdicional> adicionales;

    public TarifaCostoDTO() {
    }

    public TarifaCostoDTO(TarifaCosto tarifaCosto) {
        this.id = tarifaCosto.getId();
        this.nombre = tarifaCosto.getNombreTarifa();
        this.valorBase = tarifaCosto.getValorBase();

        this.esVigente = tarifaCosto.isEsVigente();

        this.transportistaNombre = tarifaCosto.getTransportista() != null
                ? tarifaCosto.getTransportista().getNombreComercial()
                : null;
        this.tipoVehiculoNombre = tarifaCosto.getTipoVehiculo() != null ? tarifaCosto.getTipoVehiculo().getNombre()
                : null;
        this.zonaNombre = tarifaCosto.getZonaViaje() != null ? tarifaCosto.getZonaViaje().getNombre() : null;
        this.tipoCargaNombre = tarifaCosto.getTipoCargaTarifa() != null ? tarifaCosto.getTipoCargaTarifa().getNombre()
                : null;
        this.transportistaId = tarifaCosto.getTransportista() != null ? tarifaCosto.getTransportista().getId() : null;
        this.tipoVehiculoId = tarifaCosto.getTipoVehiculo() != null ? tarifaCosto.getTipoVehiculo().getId() : null;
        this.zonaId = tarifaCosto.getZonaViaje() != null ? tarifaCosto.getZonaViaje().getId() : null;
        this.tipoCargaId = tarifaCosto.getTipoCargaTarifa() != null ? tarifaCosto.getTipoCargaTarifa().getId() : null;
        this.total = tarifaCosto.getValorTotal();
        this.adicionales = tarifaCosto.getAdicionales();
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

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public boolean isEsVigente() {
        return esVigente;
    }

    public void setEsVigente(boolean esVigente) {
        this.esVigente = esVigente;
    }

    public String getTransportistaNombre() {
        return transportistaNombre;
    }

    public void setTransportistaNombre(String transportistaNombre) {
        this.transportistaNombre = transportistaNombre;
    }

    public String getTipoVehiculoNombre() {
        return tipoVehiculoNombre;
    }

    public void setTipoVehiculoNombre(String tipoVehiculoNombre) {
        this.tipoVehiculoNombre = tipoVehiculoNombre;
    }

    public String getZonaNombre() {
        return zonaNombre;
    }

    public void setZonaNombre(String zonaNombre) {
        this.zonaNombre = zonaNombre;
    }

    public String getTipoCargaNombre() {
        return tipoCargaNombre;
    }

    public void setTipoCargaNombre(String tipoCargaNombre) {
        this.tipoCargaNombre = tipoCargaNombre;
    }

    public String getTransportistaId() {
        return transportistaId;
    }

    public void setTransportistaId(String transportistaId) {
        this.transportistaId = transportistaId;
    }

    public Long getTipoVehiculoId() {
        return tipoVehiculoId;
    }

    public void setTipoVehiculoId(Long tipoVehiculoId) {
        this.tipoVehiculoId = tipoVehiculoId;
    }

    public Long getZonaId() {
        return zonaId;
    }

    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }

    public Long getTipoCargaId() {
        return tipoCargaId;
    }

    public void setTipoCargaId(Long tipoCargaId) {
        this.tipoCargaId = tipoCargaId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<TarifaAdicional> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(List<TarifaAdicional> adicionales) {
        this.adicionales = adicionales;
    }
}