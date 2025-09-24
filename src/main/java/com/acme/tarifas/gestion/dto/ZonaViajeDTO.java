package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.ZonaViaje;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZonaViajeDTO {
    private Long id;
    private String nombre;

    public ZonaViajeDTO(ZonaViaje zona) {
        this.id = zona.getId();
        this.nombre = zona.getNombre();
    }
}
