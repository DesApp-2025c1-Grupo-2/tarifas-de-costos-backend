package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TarifasCosto")
public class TarifaCosto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TarifaCosto")
    private Long id;

    @Column(unique = true)
    private String codigoTarifa;

    @NotBlank(message = "El nombre es obligatorio.")
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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;


    @NotNull
    @Column(name = "esVigente", nullable = false)
    private boolean esVigente = true;

    private Integer version;

    @OneToMany(mappedBy = "tarifaCosto", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<TarifaAdicional> adicionales = new ArrayList<>();

    @Transient
    public Double getValorTotal() {
        if (this.valorBase == null)
            return 0.0;
        double adicionalesTotal = (this.adicionales == null) ? 0.0
                : this.adicionales.stream()
                        .mapToDouble(TarifaAdicional::getCostoEspecifico)
                        .sum();
        return this.valorBase + adicionalesTotal;
    }

    public boolean isEsVigente() {
        return Boolean.TRUE.equals(this.esVigente);
    }
}