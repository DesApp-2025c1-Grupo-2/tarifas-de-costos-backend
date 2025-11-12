// Archivo: src/main/java/com/acme/tarifas/gestion/dto/HistorialServicioDTO.java
package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.*;
// Quitar import de EntityNotFoundException si ya no se usa
// import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

// Añadir getters y setters con Lombok o manualmente si es necesario
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Añadir constructor vacío para posible uso futuro

@Getter
@Setter
@NoArgsConstructor // Lombok generará constructor vacío
public class HistorialServicioDTO {
    private Long id; // ID del registro de historial
    private LocalDateTime fecha; // Fecha de la modificación
    private String nombreTarifaUtilizada; // Nombre de la tarifa en ese momento
    private Double valorTotalTarifa; // Valor base + adicionales en ese momento (aproximado, ya que adicionales no están en historial)
    private String nombreCarga; // Nombre del tipo de carga en ese momento

    // --- CONSTRUCTOR MODIFICADO ---
    public HistorialServicioDTO(TarifaCostoHistorial historial) {
        this.id = historial.getId(); // Usar el ID del historial
        this.fecha = historial.getFechaModificacion(); // Fecha del cambio
        this.nombreTarifaUtilizada = historial.getNombreTarifa(); // Nombre guardado en el historial

        // El historial solo guarda el valorBase. No tenemos los adicionales exactos
        // de ese momento. Mostramos el valorBase como referencia.
        // Si necesitas el total exacto, habría que guardar más info en el historial.
        this.valorTotalTarifa = historial.getValorBase();

        // Obtener nombre de la carga desde la entidad asociada en el historial
        if (historial.getTipoCargaTarifa() != null) {
            this.nombreCarga = historial.getTipoCargaTarifa().getNombre();
        } else {
            this.nombreCarga = "N/A"; // Si no había tipo de carga asignado
        }
    }

    // Lombok generará getters y setters, así que se pueden quitar los manuales
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // ... etc ...
}