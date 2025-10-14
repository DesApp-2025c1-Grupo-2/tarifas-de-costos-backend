package com.acme.tarifas.gestion.entity;

import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
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

    @Transient
    private TipoVehiculoDTO tipoVehiculo;
    
    // CORRECCIÓN: Campo persistente para TipoVehiculo ID (Ahora opcional)
    @Column(name = "ID_TipoVehiculo", length = 255) 
    private String tipoVehiculoId;

    @ManyToOne
    @JoinColumn(name = "ID_TipoCargaTarifa")
    private TipoCargaTarifa tipoCargaTarifa;

    @ManyToOne
    @JoinColumn(name = "ID_ZonaViaje")
    private ZonaViaje zonaViaje;


    @Transient
    private TransportistaDTO transportista;

    // CORRECCIÓN: Campo persistente para Transportista ID (Ahora opcional)
    @Column(name = "ID_Transportista", length = 255)
    private String transportistaId;

    private Double valorBase;
    private LocalDateTime fechaModificacion;

    @Column(name = "ID_UsuarioModifico")
    private Integer idUsuarioModifico;

    private String comentarioCambio;
}