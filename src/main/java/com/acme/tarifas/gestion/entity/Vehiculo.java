package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VehiculoEspecifico")
    private Long id;

    @Column(unique = true)
    private String patenteDominio;

    @ManyToOne
    @JoinColumn(name = "ID_TipoVehiculo")
    private TipoVehiculo tipoVehiculo;

    @ManyToOne
    @JoinColumn(name = "ID_TransportistaPropietario")
    private Transportista transportistaPropietario;

    private String marca;
    private String modelo;
    private Integer anioFabricacion;
    private String descripcionAdicional;
    private LocalDateTime fechaAltaSistema;
    private Boolean activo;
}
