package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para transportistaDTO y CamionDTO
@Data
public class ContactoDTO {

    private String nombre;
    private String email;
    private TelefonoDTO telefono;
    private String _id;

    @JsonProperty("id")
    public String getId() {
        return _id;
    }
}