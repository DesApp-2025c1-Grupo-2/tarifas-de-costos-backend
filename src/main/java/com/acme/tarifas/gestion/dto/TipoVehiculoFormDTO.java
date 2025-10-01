package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
public class TipoVehiculoFormDTO {
    private String id;
    private String nombre;

    public TipoVehiculoFormDTO(TipoVehiculoDTO dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
    }
} 