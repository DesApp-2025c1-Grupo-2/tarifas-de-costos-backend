package com.acme.tarifas.gestion.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ZonaViajeDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String regionMapa;
    private Boolean activo = true;
    private Set<String> provinciasNombres;
}