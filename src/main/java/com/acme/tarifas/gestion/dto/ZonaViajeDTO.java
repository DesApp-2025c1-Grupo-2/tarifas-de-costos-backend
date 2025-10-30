package com.acme.tarifas.gestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; // Añadir validaciones si se usa para entrada
import jakarta.validation.constraints.NotEmpty; // Para validar la lista de provinciasNombres
import java.util.Set;

@Data
@NoArgsConstructor
public class ZonaViajeDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    // ELIMINADO: private String regionMapa;

    private Boolean activo = true; // Por defecto activo al crear desde DTO

    // Asegura que al menos una provincia sea enviada si es requerido por negocio
    // @NotEmpty(message = "Debe seleccionar al menos una provincia")
    private Set<String> provinciasNombres;

    // Constructor desde Entidad (opcional, el mapeo se hace en el servicio)
    // public ZonaViajeDTO(ZonaViaje zona) { ... } // Quitado para hacer mapeo en
    // servicio
}