package com.acme.tarifas.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TarifaCostoDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private Double valorBase;

    private Long transportistaId;
    private String transportistaNombre;

    private Long zonaId;
    private String zonaNombre;

    private Long tipoCargaId;
    private String tipoCargaNombre;

    private Long tipoVehiculoId;
    private String tipoVehiculoNombre;

}
