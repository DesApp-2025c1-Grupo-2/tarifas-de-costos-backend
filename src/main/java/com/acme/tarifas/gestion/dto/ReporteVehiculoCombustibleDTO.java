// Archivo: src/main/java/com/acme/tarifas/gestion/dto/ReporteVehiculoCombustibleDTO.java
package com.acme.tarifas.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}