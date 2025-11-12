package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @Column(unique = true) // Asegura unicidad del nombre si es necesario
    private String nombre;

    private String descripcion;

    // ELIMINADO: @NotBlank(message = "La region es obligatoria.")
    // ELIMINADO: private String regionMapa;

    @ManyToMany(fetch = FetchType.LAZY) // Mantenido LAZY, se manejará con DTO/Inicialización
    @JoinTable(name = "ZonaProvincia", joinColumns = @JoinColumn(name = "ID_ZonaViaje"), inverseJoinColumns = @JoinColumn(name = "ID_Provincia"))
    private Set<Provincia> provincias = new HashSet<>();

    @NotNull
    @Column(name = "Activo", nullable = false) // Especificar nullable=false es buena práctica
    private Boolean activo = true;

    // Método 'isActivo()' renombrado para evitar conflictos con Lombok y seguir
    // convención
    public boolean getActivo() {
        return Boolean.TRUE.equals(this.activo);
    }

    // Setter explícito si necesitas lógica especial, sino Lombok lo genera
    public void setActivo(Boolean activo) {
        this.activo = (activo != null) ? activo : true; // Asegura que no sea null
    }

    // Sobrescribir equals y hashCode si manejas la entidad en Sets/Maps fuera de
    // JPA
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ZonaViaje zonaViaje = (ZonaViaje) o;
        return id != null && id.equals(zonaViaje.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // O usa Objects.hash(id) si id nunca es null post-persistencia
    }
}