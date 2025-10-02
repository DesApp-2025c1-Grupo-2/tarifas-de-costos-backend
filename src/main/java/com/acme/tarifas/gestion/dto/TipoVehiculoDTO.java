package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TipoVehiculoDTO{
    private String _id;

    @JsonProperty("id")
    @JsonAlias("_id")
    private String id;


    private String nombre;


    private String descripcion;
    private String deletedAt;

    @JsonProperty("licencia_permitida")
    private String licenciaPermitida;
}
