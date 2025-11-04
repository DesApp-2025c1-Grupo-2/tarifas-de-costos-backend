package com.acme.tarifas.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVehiculoCombustibleDTO {

    private String vehiculoPatente;
    private Long cantidadViajes;
    private Long cantidadCargasCombustible;
    private Double costoTotalCombustible;
    private String fechaInicio;
    private String fechaFin;
    private Double viajesPorCarga;
    private Double totalKilometros;
    private Double litrosTotales;

    private List<ViajeEventoDTO> viajes;
    private List<CargaEventoDTO> cargas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViajeEventoDTO {
        private LocalDate fecha;
        private Double km;
        private String estado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargaEventoDTO {
        private LocalDate fecha;
        private Double litros;
    }
}