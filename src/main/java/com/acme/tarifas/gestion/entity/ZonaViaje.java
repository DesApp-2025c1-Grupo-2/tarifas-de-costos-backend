package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ZonasViaje")
public class ZonaViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ZonaViaje")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(max=75, message = "'Descripcion' no puede tener mas de 75 caracteres")
    private String descripcion;

    @NotBlank(message = "La region es obligatoria.")
    private String regionMapa;

    @NotNull
    @Column(name = "Activo")
    private Boolean activo = true;

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo);
    }

}
