package com.acme.tarifas.gestion.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VariacionTarifaDTO {
    private Long tarifaId;
    private String nombreTarifa;
    private Double valorInicial;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicial;

    private Double valorFinal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinal;
    
    private Double variacionAbsoluta;
    private Double variacionPorcentual;

    public VariacionTarifaDTO(Long tarifaId, String nombreTarifa) {
        this.tarifaId = tarifaId;
        this.nombreTarifa = nombreTarifa;
    }
}