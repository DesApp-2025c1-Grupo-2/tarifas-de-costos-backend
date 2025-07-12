package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transportistas")
public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Transportista")
    private Long id;

    @NotBlank(message = "El CUIT es obligatorio.")
    @Column(name = "CUIT", unique = true, nullable = false)
    private String cuit;

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Column(name = "NombreEmpresa")
    private String nombreEmpresa;

    @NotBlank(message = "El nombre del contacto es obligatorio")
    @Column(name = "ContactoNombre")
    private String contactoNombre;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Column(name = "ContactoEmail")
    private String contactoEmail;

    @NotNull
    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

    @NotBlank(message = "El telefono es obligatorio")
    @Column(name = "ContactoTelefono")
    private String contactoTelefono;

    @Column(name = "EvaluacionDesempeno")
    private String evaluacionDesempeno;

    
    @CreationTimestamp
    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    @ManyToMany
    @JoinTable(name = "Transportistas_ZonasOperacion", joinColumns = @JoinColumn(name = "ID_Transportista"), inverseJoinColumns = @JoinColumn(name = "ID_ZonaViaje"))
    @JsonIgnore
    private List<ZonaViaje> zonasOperacion = new ArrayList<>();
}