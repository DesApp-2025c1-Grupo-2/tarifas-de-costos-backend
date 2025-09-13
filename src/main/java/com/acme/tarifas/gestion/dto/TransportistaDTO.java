package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransportistaDTO {
    //Viene de API Viajes

    @JsonProperty("_id")
    private String id;

    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("nombre_comercial")
    private String nombreComercial;

    private String cuit;

    private DireccionDTO direccion;

    private ContactoDTO contacto;
}