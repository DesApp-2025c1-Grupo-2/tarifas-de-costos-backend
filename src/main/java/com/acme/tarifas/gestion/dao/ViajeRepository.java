package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    @Query("SELECT v FROM Viaje v LEFT JOIN FETCH v.tarifaCostoUtilizada LEFT JOIN FETCH v.cliente WHERE v.fechaViaje BETWEEN :inicio AND :fin")
    List<Viaje> findByFechaViajeBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT v FROM Viaje v JOIN FETCH v.cliente WHERE v.cliente.id = :clienteId")
    List<Viaje> findByClienteId(@Param("clienteId") Long clienteId);


    @Query("SELECT v FROM Viaje v WHERE v.tarifaCostoUtilizada.transportista.id = :transportistaId")
    List<Viaje> findByTransportistaId(@Param("transportistaId") Long transportistaId);
}