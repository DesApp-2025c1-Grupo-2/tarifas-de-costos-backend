package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para transportistaDTO
@Data
public class DireccionDTO {

    private String calle;
    private String numero;
    private String ciudad;

    @JsonProperty("estado_provincia")
    private String estadoProvincia;

    private String pais;
    private String tipo;

    @JsonProperty("_id")
    private String id;
}