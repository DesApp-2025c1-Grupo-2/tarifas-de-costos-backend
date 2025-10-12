package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import java.util.List;

public class TarifaCostoDTO {

    private Long id;
    private String nombreTarifa;
    private Double valorBase;
    private boolean esVigente;
    private String transportistaNombre;
    private String tipoVehiculoNombre;
    private String zonaNombre;
    private String tipoCargaNombre;
    private String transportistaId;
    private String tipoVehiculoId;
    private Long zonaId;
    private Long tipoCargaId;
    private Double total;
    private List<TarifaAdicional> adicionales;

    public TarifaCostoDTO() {
        // Constructor vacío. Se setea todo desde el service.
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreTarifa() { return nombreTarifa; }
    public void setNombreTarifa(String nombreTarifa) { this.nombreTarifa = nombreTarifa; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isEsVigente() { return esVigente; }
    public void setEsVigente(boolean esVigente) { this.esVigente = esVigente; }

    public String getTransportistaNombre() { return transportistaNombre; }
    public void setTransportistaNombre(String transportistaNombre) { this.transportistaNombre = transportistaNombre; }

    public String getTipoVehiculoNombre() { return tipoVehiculoNombre; }
    public void setTipoVehiculoNombre(String tipoVehiculoNombre) { this.tipoVehiculoNombre = tipoVehiculoNombre; }

    public String getZonaNombre() { return zonaNombre; }
    public void setZonaNombre(String zonaNombre) { this.zonaNombre = zonaNombre; }

    public String getTipoCargaNombre() { return tipoCargaNombre; }
    public void setTipoCargaNombre(String tipoCargaNombre) { this.tipoCargaNombre = tipoCargaNombre; }

    public String getTransportistaId() { return transportistaId; }
    public void setTransportistaId(String transportistaId) { this.transportistaId = transportistaId; }

    public String getTipoVehiculoId() { return tipoVehiculoId; }
    public void setTipoVehiculoId(String tipoVehiculoId) { this.tipoVehiculoId = tipoVehiculoId; }

    public Long getZonaId() { return zonaId; }
    public void setZonaId(Long zonaId) { this.zonaId = zonaId; }

    public Long getTipoCargaId() { return tipoCargaId; }
    public void setTipoCargaId(Long tipoCargaId) { this.tipoCargaId = tipoCargaId; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<TarifaAdicional> getAdicionales() { return adicionales; }
    public void setAdicionales(List<TarifaAdicional> adicionales) { this.adicionales = adicionales; }
}
