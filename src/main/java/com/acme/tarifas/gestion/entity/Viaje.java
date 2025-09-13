package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
"Viajes" debe venir de API de viajes,
pero hay referencias a esta entidad en HistorialServicioDTO, asi que si se elimina ahora
seguramente se rompan los reportes

Eliminar esta entidad cuando se corrija HistorialServicioDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Viajes")
public class Viaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Viaje")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_VehiculoEspecificoUtilizado")
    private Vehiculo vehiculoUtilizado;

    private LocalDate fechaViaje;

    /*
    Se eliminó cliente. revisar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Cliente", nullable = false)
    private Cliente cliente;
    */


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TarifaCostoUtilizada", nullable = false)
    @JsonIgnore
    private TarifaCosto tarifaCostoUtilizada;

    private Double precioFacturadoCliente;

    @ManyToOne
    @JoinColumn(name = "ID_TipoCargaEspecifica")
    private TipoCargaEspecifica tipoCargaEspecifica;

    private Double pesoTotalEstimadoKG;
    private Double volumenTotalEstimadoM3;
    private String requisitosEspecialesManipulacion;

    private LocalDateTime fechaRegistroSistema;
}