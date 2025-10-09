package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para transportistaDTO y CamionDTO
@Data
public class TelefonoDTO {
    @JsonProperty("codigo_pais")
    public String codigoPais;

    @JsonProperty("codigo_area")
    public String codigoArea;

    public String numero;


    @JsonProperty("id")
    @JsonAlias("_id")
    private String id;

}