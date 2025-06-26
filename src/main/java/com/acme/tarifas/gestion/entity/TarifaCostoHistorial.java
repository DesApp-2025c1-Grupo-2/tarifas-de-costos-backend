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
@Table(name = "TarifasCostoHistorial")
public class TarifaCostoHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Historial")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TarifaCostoOriginal")
    private TarifaCosto tarifaOriginal;

    private String codigoTarifa;
    private String nombreTarifa;

    @ManyToOne
    @JoinColumn(name = "ID_TipoVehiculo")
    private TipoVehiculo tipoVehiculo;

    @ManyToOne
    @JoinColumn(name = "ID_TipoCargaTarifa")
    private TipoCargaTarifa tipoCargaTarifa;

    @ManyToOne
    @JoinColumn(name = "ID_ZonaViaje")
    private ZonaViaje zonaViaje;

    @ManyToOne
    @JoinColumn(name = "ID_Transportista")
    private Transportista transportista;

    private Double valorBase;
    private LocalDateTime fechaModificacion;

    @Column(name = "ID_UsuarioModifico")
    private Integer idUsuarioModifico;

    private String comentarioCambio;
}
