package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "ContactoTelefono")
    private String contactoTelefono;

    @Column(name = "EvaluacionDesempeno")
    private String evaluacionDesempeno;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    @ManyToMany
    @JoinTable(
            name = "Transportistas_ZonasOperacion",
            joinColumns = @JoinColumn(name = "ID_Transportista"),
            inverseJoinColumns = @JoinColumn(name = "ID_ZonaViaje")
    )
    @JsonIgnore
    private List<ZonaViaje> zonasOperacion = new ArrayList<>();

    public Transportista() {
    }

    public Transportista(Long id, String nombreEmpresa, String contactoNombre, String contactoEmail, String contactoTelefono, String evaluacionDesempeno, LocalDateTime fechaRegistro, List<ZonaViaje> zonasOperacion) {
        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.contactoNombre = contactoNombre;
        this.contactoEmail = contactoEmail;
        this.contactoTelefono = contactoTelefono;
        this.evaluacionDesempeno = evaluacionDesempeno;
        this.fechaRegistro = fechaRegistro;
        this.zonasOperacion = zonasOperacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getContactoNombre() {
        return contactoNombre;
    }

    public void setContactoNombre(String contactoNombre) {
        this.contactoNombre = contactoNombre;
    }

    public String getContactoEmail() {
        return contactoEmail;
    }

    public void setContactoEmail(String contactoEmail) {
        this.contactoEmail = contactoEmail;
    }

    public String getContactoTelefono() {
        return contactoTelefono;
    }

    public void setContactoTelefono(String contactoTelefono) {
        this.contactoTelefono = contactoTelefono;
    }

    public String getEvaluacionDesempeno() {
        return evaluacionDesempeno;
    }

    public void setEvaluacionDesempeno(String evaluacionDesempeno) {
        this.evaluacionDesempeno = evaluacionDesempeno;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<ZonaViaje> getZonasOperacion() {
        return zonasOperacion;
    }

    public void setZonasOperacion(List<ZonaViaje> zonasOperacion) {
        this.zonasOperacion = zonasOperacion;
    }
}