package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TipoVehiculoDTO {

    // --- MODIFICACIÓN CLAVE ---
    // Se mapea la propiedad "_id" del JSON al campo "id" de Java.
    @JsonProperty("_id")
    private String id;

    private String nombre;
    private String descripcion;
    private String deletedAt;

    @JsonProperty("licencia_permitida")
    private String licenciaPermitida;
}