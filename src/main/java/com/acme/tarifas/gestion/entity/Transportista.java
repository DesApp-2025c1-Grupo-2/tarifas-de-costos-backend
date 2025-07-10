package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(name = "NombreEmpresa")
    private String nombreEmpresa;

    @Column(name = "ContactoNombre")
    private String contactoNombre;

    @Column(name = "ContactoEmail")
    private String contactoEmail;

    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

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