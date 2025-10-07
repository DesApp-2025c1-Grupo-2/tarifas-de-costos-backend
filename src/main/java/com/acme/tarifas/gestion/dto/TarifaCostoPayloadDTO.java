package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import lombok.Data;
import java.util.List;

@Data
public class TarifaCostoPayloadDTO {
    private String nombreTarifa;
    private Double valorBase;
    private String transportistaId;
    private String tipoVehiculoId;
    private Long zonaId;
    private Long tipoCargaId;
    private List<TarifaAdicional> adicionales;
}