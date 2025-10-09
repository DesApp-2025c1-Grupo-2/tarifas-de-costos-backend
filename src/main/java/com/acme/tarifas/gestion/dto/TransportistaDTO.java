package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransportistaDTO {

    @JsonProperty("id")
    @JsonAlias("_id")
    private String id;


    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("nombre_comercial")
    private String nombreComercial;

    private String cuit;

    private DireccionDTO direccion;

    private ContactoDTO contacto;
}