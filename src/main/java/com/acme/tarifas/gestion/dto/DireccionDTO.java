package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para transportistaDTO y CamionDTO
@Data
public class DireccionDTO {

    private String calle;
    private String numero;
    private String ciudad;

    @JsonProperty("estado_provincia")
    private String estadoProvincia;

    private String pais;
    private String tipo;

    private String _id;

    @JsonProperty("id")
    public String getId() {
        return _id;
    }
}