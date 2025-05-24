package com.acme.tarifas.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Cliente", nullable = false)
    private Cliente cliente;

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

    public Viaje() {
    }

    public Viaje(Long id, Vehiculo vehiculoUtilizado, LocalDate fechaViaje, Cliente cliente, TarifaCosto tarifaCostoUtilizada, Double precioFacturadoCliente, TipoCargaEspecifica tipoCargaEspecifica, Double pesoTotalEstimadoKG, Double volumenTotalEstimadoM3, String requisitosEspecialesManipulacion, LocalDateTime fechaRegistroSistema) {
        this.id = id;
        this.vehiculoUtilizado = vehiculoUtilizado;
        this.fechaViaje = fechaViaje;
        this.cliente = cliente;
        this.tarifaCostoUtilizada = tarifaCostoUtilizada;
        this.precioFacturadoCliente = precioFacturadoCliente;
        this.tipoCargaEspecifica = tipoCargaEspecifica;
        this.pesoTotalEstimadoKG = pesoTotalEstimadoKG;
        this.volumenTotalEstimadoM3 = volumenTotalEstimadoM3;
        this.requisitosEspecialesManipulacion = requisitosEspecialesManipulacion;
        this.fechaRegistroSistema = fechaRegistroSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculoUtilizado() {
        return vehiculoUtilizado;
    }

    public void setVehiculoUtilizado(Vehiculo vehiculoUtilizado) {
        this.vehiculoUtilizado = vehiculoUtilizado;
    }

    public LocalDate getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(LocalDate fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TarifaCosto getTarifaCostoUtilizada() {
        return tarifaCostoUtilizada;
    }

    public void setTarifaCostoUtilizada(TarifaCosto tarifaCostoUtilizada) {
        this.tarifaCostoUtilizada = tarifaCostoUtilizada;
    }

    public Double getPrecioFacturadoCliente() {
        return precioFacturadoCliente;
    }

    public void setPrecioFacturadoCliente(Double precioFacturadoCliente) {
        this.precioFacturadoCliente = precioFacturadoCliente;
    }

    public TipoCargaEspecifica getTipoCargaEspecifica() {
        return tipoCargaEspecifica;
    }

    public void setTipoCargaEspecifica(TipoCargaEspecifica tipoCargaEspecifica) {
        this.tipoCargaEspecifica = tipoCargaEspecifica;
    }

    public Double getPesoTotalEstimadoKG() {
        return pesoTotalEstimadoKG;
    }

    public void setPesoTotalEstimadoKG(Double pesoTotalEstimadoKG) {
        this.pesoTotalEstimadoKG = pesoTotalEstimadoKG;
    }

    public Double getVolumenTotalEstimadoM3() {
        return volumenTotalEstimadoM3;
    }

    public void setVolumenTotalEstimadoM3(Double volumenTotalEstimadoM3) {
        this.volumenTotalEstimadoM3 = volumenTotalEstimadoM3;
    }

    public String getRequisitosEspecialesManipulacion() {
        return requisitosEspecialesManipulacion;
    }

    public void setRequisitosEspecialesManipulacion(String requisitosEspecialesManipulacion) {
        this.requisitosEspecialesManipulacion = requisitosEspecialesManipulacion;
    }

    public LocalDateTime getFechaRegistroSistema() {
        return fechaRegistroSistema;
    }

    public void setFechaRegistroSistema(LocalDateTime fechaRegistroSistema) {
        this.fechaRegistroSistema = fechaRegistroSistema;
    }
}