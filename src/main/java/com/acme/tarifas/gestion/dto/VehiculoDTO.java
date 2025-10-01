package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehiculoDTO {

    // --- INICIO DE LA MODIFICACIÓN ---
    // Se mapea la propiedad "_id" del JSON al campo "id" de Java.
    @JsonProperty("_id")
    private String id;
    // --- FIN DE LA MODIFICACIÓN ---

    private String patente;
    private String marca;
    private String modelo;

    @JsonProperty("año")
    private Integer anio;

    @JsonProperty("volumen_carga")
    private Integer volumenCarga;

    @JsonProperty("peso_carga")
    private Integer pesoCarga;

    private TipoVehiculoDTO tipo;

    private String deletedAt;

    @JsonProperty("empresa")
    private TransportistaDTO transportista;
}