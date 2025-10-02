package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ContactoDTO {

    @JsonProperty("_id")
    private String id;

    private String nombre;
    private String email;
    private TelefonoDTO telefono;

    @JsonProperty("telefono")
    private void unpackTelefono(JsonNode telefonoNode) {
        if (telefonoNode.isObject()) {
            this.telefono = new TelefonoDTO();
            this.telefono.setCodigoPais(telefonoNode.has("codigo_pais") ? telefonoNode.get("codigo_pais").asText() : null);
            this.telefono.setCodigoArea(telefonoNode.has("codigo_area") ? telefonoNode.get("codigo_area").asText() : null);
            this.telefono.setNumero(telefonoNode.has("numero") ? telefonoNode.get("numero").asText() : null);
            this.telefono.setId(telefonoNode.has("_id") ? telefonoNode.get("_id").asText() : null);
        }
        // Si es un string (ID), lo ignoramos por ahora para evitar que la app se rompa.
    }
}