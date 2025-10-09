package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DireccionDTO {

    @JsonProperty("_id")
    private String id;

    private String calle;
    private String numero;
    private String ciudad;

    @JsonProperty("estado_provincia")
    private String estadoProvincia;

    private String pais;
    private String tipo;
}