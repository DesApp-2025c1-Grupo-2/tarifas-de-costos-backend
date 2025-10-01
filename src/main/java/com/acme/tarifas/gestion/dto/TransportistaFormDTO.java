package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
public class TransportistaFormDTO {
    private String id;
    private String nombre;

    public TransportistaFormDTO(TransportistaDTO dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombreComercial();
    }
} 