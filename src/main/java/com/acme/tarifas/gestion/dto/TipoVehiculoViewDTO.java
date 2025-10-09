package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TipoVehiculoViewDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private Double capacidadPesoKG;
    private Double capacidadVolumenM3;
    private boolean activo = true;

    public TipoVehiculoViewDTO(TipoVehiculoDTO dto) {
        if (dto != null) {
            this.id = dto.getId();
            this.nombre = dto.getNombre();
            this.descripcion = dto.getDescripcion();
        }
    }

    public TipoVehiculoViewDTO(VehiculoDTO vehiculo, TipoVehiculoDTO tipo) {
        if (tipo != null) {
            this.id = tipo.getId();
            this.nombre = tipo.getNombre();
            this.descripcion = tipo.getDescripcion();
        }
        if (vehiculo != null) {
            this.capacidadPesoKG = vehiculo.getPesoCarga();
            this.capacidadVolumenM3 = vehiculo.getVolumenCarga();
        }
    }
}