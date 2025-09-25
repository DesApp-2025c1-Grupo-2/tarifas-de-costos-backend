package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransportistaDTO {

    private String _id;

    @JsonProperty("id")
    public String getId() {
        return _id;
    }
    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("nombre_comercial")
    private String nombreComercial;

    private String cuit;

    private DireccionDTO direccion;

    private ContactoDTO contacto;
}