package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.*;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistorialServicioDTO {
    private Long id;
    private LocalDateTime fecha; 
    private String nombreTarifaUtilizada;
    private Double valorTotalTarifa;
    private String nombreCarga;

    public HistorialServicioDTO(Viaje viaje) {
        this.id = viaje.getId();

        try {
            TarifaCosto tarifa = viaje.getTarifaCostoUtilizada();
            if (tarifa != null) {
                this.nombreTarifaUtilizada = tarifa.getNombreTarifa();
                this.valorTotalTarifa = tarifa.getValorTotal();

                this.fecha = tarifa.getFechaCreacion(); 


                if (tarifa.getTipoCargaTarifa() != null) {
                    this.nombreCarga = tarifa.getTipoCargaTarifa().getNombre();
                } else {
                    this.nombreCarga = "N/A (en tarifa)";
                }

            }
        } catch (EntityNotFoundException e) {
            this.nombreTarifaUtilizada = "[Tarifa Eliminada]";
            this.valorTotalTarifa = 0.0;
            this.nombreCarga = "[Info no disponible]";
            this.fecha = null;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getNombreTarifaUtilizada() { return nombreTarifaUtilizada; }
    public void setNombreTarifaUtilizada(String nombreTarifaUtilizada) { this.nombreTarifaUtilizada = nombreTarifaUtilizada; }
    public Double getValorTotalTarifa() { return valorTotalTarifa; }
    public void setValorTotalTarifa(Double valorTotalTarifa) { this.valorTotalTarifa = valorTotalTarifa; }
    public String getNombreCarga() { return nombreCarga; }
    public void setNombreCarga(String nombreCarga) { this.nombreCarga = nombreCarga; }
}