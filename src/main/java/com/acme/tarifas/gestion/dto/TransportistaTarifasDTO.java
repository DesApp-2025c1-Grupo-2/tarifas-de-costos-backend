package com.acme.tarifas.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//Sirve para el REPORTE de transportistas
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaTarifasDTO {
    private String nombreTransportista;
    private long cantidadTarifas;
}