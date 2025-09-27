package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    private String descripcion;

    @NotBlank(message = "La region es obligatoria.")
    private String regionMapa;

    @ManyToMany
    @JoinTable(name = "ZonaProvincia", joinColumns = @JoinColumn(name = "ID_ZonaViaje"), inverseJoinColumns = @JoinColumn(name = "ID_Provincia"))
    private Set<Provincia> provincias = new HashSet<>();

    @NotNull
    @Column(name = "Activo")
    private Boolean activo = true;

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo);
    }

}