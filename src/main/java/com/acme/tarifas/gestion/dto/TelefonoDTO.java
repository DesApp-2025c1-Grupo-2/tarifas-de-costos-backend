package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TelefonoDTO {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("codigo_pais")
    public String codigoPais;

    @JsonProperty("codigo_area")
    public String codigoArea;

    public String numero;
}