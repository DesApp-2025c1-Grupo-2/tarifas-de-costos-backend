package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TipoVehiculoViewDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private Integer capacidadPesoKG;
    private Integer capacidadVolumenM3;
    private boolean activo = true;

    public TipoVehiculoViewDTO(VehiculoDTO dto) {
        // --- INICIO DE LA MODIFICACIÓN ---
        // Se añaden validaciones robustas para manejar datos nulos o incompletos
        if (dto != null) {
            if (dto.getTipo() != null) {
                this.id = dto.getTipo().getId();
                this.nombre = dto.getTipo().getNombre();
                this.descripcion = dto.getTipo().getDescripcion();
            }
            this.capacidadPesoKG = dto.getPesoCarga();
            this.capacidadVolumenM3 = dto.getVolumenCarga();
        }
        // --- FIN DE LA MODIFICACIÓN ---
    }
}