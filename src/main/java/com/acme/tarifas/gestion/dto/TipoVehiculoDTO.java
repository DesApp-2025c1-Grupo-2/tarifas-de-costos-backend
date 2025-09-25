package com.acme.tarifas.gestion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TipoVehiculoDTO{
    private String _id;

    @JsonProperty("id")
    public String getId() {
        return _id;
    }

    public void setId(String id){
        this._id = id;
    }


    private String nombre;


    private String descripcion;
    private String deletedAt;

    @JsonProperty("licencia_permitida")
    private String licenciaPermitida;
}
