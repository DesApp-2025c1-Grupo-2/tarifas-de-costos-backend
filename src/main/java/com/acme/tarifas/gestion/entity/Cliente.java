package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
