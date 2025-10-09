package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransportistaDTO {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("nombre_comercial")
    private String nombreComercial;

    @JsonProperty("cuit")
    private String cuit;

    @JsonProperty("contacto")
    private ContactoDTO contacto;

    /**
     * Método clave: Devuelve el nombre que está dentro del objeto 'contacto'.
     * Este es el nombre que queremos mostrar (ej: "Laura Acosta").
     * Si el contacto no existe o no tiene nombre, devuelve el nombre comercial como
     * alternativa.
     * 
     * @return El nombre del contacto o el nombre comercial de la empresa.
     */
    public String getNombreParaMostrar() {
        if (this.contacto != null && this.contacto.getNombre() != null && !this.contacto.getNombre().isEmpty()) {
            return this.contacto.getNombre();
        }
        if (this.nombreComercial != null && !this.nombreComercial.isEmpty()) {
            return this.nombreComercial;
        }
        return ""; 
    }
}