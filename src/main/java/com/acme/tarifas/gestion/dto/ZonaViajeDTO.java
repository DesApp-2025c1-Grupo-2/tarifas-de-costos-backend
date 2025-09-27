package com.acme.tarifas.gestion.dto;

import com.acme.tarifas.gestion.entity.ZonaViaje;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class ZonaViajeDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String regionMapa;
    private Boolean activo = true;
    private Set<String> provinciasNombres;


    public ZonaViajeDTO(ZonaViaje zona) {
        this.id = zona.getId();
        this.nombre = zona.getNombre();
        this.descripcion = zona.getDescripcion();
        this.regionMapa = zona.getRegionMapa();
        this.activo = zona.isActivo();
        //ARREGLAR: this.provinciasNombres = zona.getProvincias();
    }
}
