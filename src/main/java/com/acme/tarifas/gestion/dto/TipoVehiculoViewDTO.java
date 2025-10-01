package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TipoVehiculoViewDTO {
    private String id;
    private String nombre;
    private String descripcion;
    // Estos campos se mantienen por compatibilidad, aunque no siempre vendrán
    // llenos.
    private Integer capacidadPesoKG;
    private Integer capacidadVolumenM3;
    private boolean activo = true;

    /**
     * Constructor para cuando se recibe un TipoVehiculoDTO directamente.
     * Esto es más eficiente y se usa en la tabla de "Vehículos".
     */
    public TipoVehiculoViewDTO(TipoVehiculoDTO dto) {
        if (dto != null) {
            this.id = dto.getId();
            this.nombre = dto.getNombre();
            this.descripcion = dto.getDescripcion();
        }
    }

    /**
     * Constructor para cuando se recibe un VehiculoDTO completo.
     * Se mantiene por si alguna otra parte del código lo necesita.
     */
    public TipoVehiculoViewDTO(VehiculoDTO dto) {
        if (dto != null) {
            if (dto.getTipo() != null) {
                this.id = dto.getTipo().getId();
                this.nombre = dto.getTipo().getNombre();
                this.descripcion = dto.getTipo().getDescripcion();
            }
            this.capacidadPesoKG = dto.getPesoCarga();
            this.capacidadVolumenM3 = dto.getVolumenCarga();
        }
    }
}