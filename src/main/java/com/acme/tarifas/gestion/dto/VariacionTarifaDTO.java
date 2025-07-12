package com.acme.tarifas.gestion.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VariacionTarifaDTO {
    private Long tarifaId;
    private String nombreTarifa;
    private Double valorInicial;
    private LocalDateTime fechaInicial;
    private Double valorFinal;
    private LocalDateTime fechaFinal;
    private Double variacionAbsoluta;
    private Double variacionPorcentual;

    public VariacionTarifaDTO(Long tarifaId, String nombreTarifa) {
        this.tarifaId = tarifaId;
        this.nombreTarifa = nombreTarifa;
    }
}