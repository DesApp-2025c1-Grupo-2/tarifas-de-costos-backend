package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransportistaDTO {

    private String _id;

    @JsonProperty("id")
    public String getId() {
        return _id;
    }

    // --- INICIO DE LA MODIFICACIÓN ---
    // Se añade el método 'setId' para que Jackson pueda deserializar el ID
    @JsonProperty("id")
    public void setId(String id) {
        this._id = id;
    }
    // --- FIN DE LA MODIFICACIÓN ---

    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("nombre_comercial")
    private String nombreComercial;

    private String cuit;

    private DireccionDTO direccion;

    private ContactoDTO contacto;
}