package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarifaCostoRepository extends JpaRepository<TarifaCosto, Long> {
    @Query("SELECT new com.acme.tarifas.gestion.dto.TarifaCostoDTO(" +
            "t.id, t.codigoTarifa, t.nombreTarifa, t.valorBase, " +
            "tr.id, tr.nombreEmpresa, z.id, z.nombre, tc.id, tc.nombre, tv.id, tv.nombre) " +
            "FROM TarifaCosto t " +
            "JOIN t.transportista tr " +
            "JOIN t.zonaViaje z " +
            "JOIN t.tipoCargaTarifa tc " +
            "JOIN t.tipoVehiculo tv " +
            "WHERE (:tipoVehiculo IS NULL OR tv.id = :tipoVehiculo) " +
            "AND (:zona IS NULL OR z.id = :zona) " +
            "AND (:tipoCarga IS NULL OR tc.id = :tipoCarga) " +
            "AND (:transportista IS NULL OR tr.id = :transportista)")
    List<TarifaCostoDTO> findByFilters(
            @Param("tipoVehiculo") Long tipoVehiculo,
            @Param("zona") Long zona,
            @Param("tipoCarga") Long tipoCarga,
            @Param("transportista") Long transportista);



    @Query("SELECT t FROM TarifaCosto t WHERE t.zonaViaje.id = :zonaId")
    List<TarifaCosto> findByZonaViajeId(@Param("zonaId") Long zonaId);

    @Query("SELECT t FROM TarifaCosto t JOIN FETCH t.adicionales WHERE t.zonaViaje = :zona")
    List<TarifaCosto> findByZonaViaje(@Param("zona") ZonaViaje zona);

    @Query("SELECT new com.acme.tarifas.gestion.dto.TarifaCostoDTO(" +
            "t.id, t.codigoTarifa, t.nombreTarifa, t.valorBase, " +
            "tr.id, tr.nombreEmpresa, " +
            "z.id, z.nombre, " +
            "tc.id, tc.nombre, " +
            "tv.id, tv.nombre) " +
            "FROM TarifaCosto t " +
            "JOIN t.transportista tr " +
            "JOIN t.zonaViaje z " +
            "JOIN t.tipoCargaTarifa tc " +
            "JOIN t.tipoVehiculo tv " +
            "WHERE t.id = :id")
    Optional<TarifaCostoDTO> findTarifaDTOById(@Param("id") Long id);

}