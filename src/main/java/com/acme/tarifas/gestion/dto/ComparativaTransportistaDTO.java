package com.acme.tarifas.gestion.dto;

import java.util.List;

public class ComparativaTransportistaDTO {

    private String servicio;
    private List<Comparativa> comparativas;

    public ComparativaTransportistaDTO() {
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public List<Comparativa> getComparativas() {
        return comparativas;
    }

    public void setComparativas(List<Comparativa> comparativas) {
        this.comparativas = comparativas;
    }

    public static class Comparativa {
        private String transportista;
        private Double costo;
        private Long tarifaId;
        private String nombreTarifa;

        public Comparativa() {
        }

        public Long getTarifaId() { return tarifaId; }
        public void setTarifaId(Long tarifaId) { this.tarifaId = tarifaId; }

        public String getNombreTarifa() { return nombreTarifa; }
        public void setNombreTarifa(String nombreTarifa) { this.nombreTarifa = nombreTarifa; }

        public String getTransportista() {
            return transportista;
        }

        public void setTransportista(String transportista) {
            this.transportista = transportista;
        }

        public Double getCosto() {
            return costo;
        }

        public void setCosto(Double costo) {
            this.costo = costo;
        }
    }
}