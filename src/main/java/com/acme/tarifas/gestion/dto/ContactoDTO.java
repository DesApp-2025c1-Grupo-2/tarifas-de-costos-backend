package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para transportistaDTO
@Data
public class ContactoDTO {

    private String nombre;
    private String email;
    private TelefonoDTO telefono;
    @JsonProperty("_id")
    private String id;
}