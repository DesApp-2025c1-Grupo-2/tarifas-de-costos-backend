package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cliente")
    private Long id;

    @Column(name = "NombreCliente", nullable = false)
    private String nombreCliente;

    @Column(name = "ContactoNombre", nullable = false)
    private String contactoNombre;

    @Column(name = "ContactoEmail", nullable = false)
    private String contactoEmail;

    @Column(name = "ContactoTelefono", nullable = false)
    private String contactoTelefono;

    @CreationTimestamp
    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public Cliente() {
    }

    public Cliente(Long id, String nombreCliente, String contactoNombre, String contactoEmail, String contactoTelefono, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.contactoNombre = contactoNombre;
        this.contactoEmail = contactoEmail;
        this.contactoTelefono = contactoTelefono;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}