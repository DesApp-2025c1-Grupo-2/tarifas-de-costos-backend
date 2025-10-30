
package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial; 

@Data
@NoArgsConstructor
public class TarifaCostoHistorialDTO {
    private Long id;
    private Long tarifaOriginalId;
    private String codigoTarifa;
    private String nombreTarifa;
    private String tipoVehiculoId;
    private String transportistaId;
    private String transportistaNombre; 
    private Long tipoCargaId;
    private String tipoCargaNombre;
    private Long zonaViajeId;
    private String zonaViajeNombre;
    private Double valorBase;
    private LocalDateTime fechaModificacion;
    private Integer idUsuarioModifico;

    public TarifaCostoHistorialDTO(TarifaCostoHistorial entity, String transportistaNombre) { 
        this.id = entity.getId();
        this.tarifaOriginalId = entity.getTarifaOriginal() != null ? entity.getTarifaOriginal().getId() : null;
        this.codigoTarifa = entity.getCodigoTarifa();
        this.nombreTarifa = entity.getNombreTarifa();
        this.tipoVehiculoId = entity.getTipoVehiculoId();
        this.transportistaId = entity.getTransportistaId();
        this.transportistaNombre = transportistaNombre; 
        this.tipoCargaId = entity.getTipoCargaTarifa() != null ? entity.getTipoCargaTarifa().getId() : null;
        this.tipoCargaNombre = entity.getTipoCargaTarifa() != null ? entity.getTipoCargaTarifa().getNombre() : "N/A";
        this.zonaViajeId = entity.getZonaViaje() != null ? entity.getZonaViaje().getId() : null;
        this.zonaViajeNombre = entity.getZonaViaje() != null ? entity.getZonaViaje().getNombre() : "N/A";
        this.valorBase = entity.getValorBase();
        this.fechaModificacion = entity.getFechaModificacion();
        this.idUsuarioModifico = entity.getIdUsuarioModifico();
        
    }
}