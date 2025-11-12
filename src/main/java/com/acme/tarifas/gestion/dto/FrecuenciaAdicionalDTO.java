package com.acme.tarifas.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrecuenciaAdicionalDTO {
    private String nombreAdicional;
    private long cantidad;
}