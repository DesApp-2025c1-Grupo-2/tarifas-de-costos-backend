package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//Se usa para contactoDTO
@Data
public class TelefonoDTO {
    @JsonProperty("codigo_pais")
    public String codigoPais;

    @JsonProperty("codigo_area")
    public String codigoArea;

    public String numero;

    @JsonProperty("_id")
    public String id;
}