package com.acme.tarifas.gestion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tarifas_Adicionales",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ID_TarifaCosto", "ID_Adicional"}))
public class TarifaAdicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Tarifa_Adicional")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TarifaCosto")
    private TarifaCosto tarifaCosto;

    @ManyToOne
    @JoinColumn(name = "ID_Adicional")
    private Adicional adicional;

    private Double costoEspecifico;

    public TarifaAdicional() {
    }

    public TarifaAdicional(Long id, TarifaCosto tarifaCosto, Adicional adicional, Double costoEspecifico) {
        this.id = id;
        this.tarifaCosto = tarifaCosto;
        this.adicional = adicional;
        this.costoEspecifico = costoEspecifico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TarifaCosto getTarifaCosto() {
        return tarifaCosto;
    }

    public void setTarifaCosto(TarifaCosto tarifaCosto) {
        this.tarifaCosto = tarifaCosto;
    }

    public Adicional getAdicional() {
        return adicional;
    }

    public void setAdicional(Adicional adicional) {
        this.adicional = adicional;
    }

    public Double getCostoEspecifico() {
        return costoEspecifico;
    }

    public void setCostoEspecifico(Double costoEspecifico) {
        this.costoEspecifico = costoEspecifico;
    }
}