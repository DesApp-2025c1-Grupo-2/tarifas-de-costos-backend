package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarifaCostoRepository extends JpaRepository<TarifaCosto, Long> {

    @Query("SELECT t FROM TarifaCosto t WHERE " +
            "(:tipoVehiculo IS NULL OR t.tipoVehiculo.id = :tipoVehiculo) AND " +
            "(:zona IS NULL OR t.zonaViaje.id = :zona) AND " +
            "(:transportista IS NULL OR t.transportista.id = :transportista) " +
            "ORDER BY t.fechaCreacion DESC")
    List<TarifaCosto> findByFilters(
            @Param("tipoVehiculo") Long tipoVehiculo,
            @Param("zona") Long zona,
            @Param("transportista") Long transportista);

    @Query("SELECT t FROM TarifaCosto t JOIN FETCH t.adicionales WHERE t.id = :id")
    Optional<TarifaCosto> findByIdWithAdicionales(@Param("id") Long id);

    boolean existsByCodigoTarifa(String codigoTarifa);

    @Query("SELECT t FROM TarifaCosto t WHERE t.zonaViaje.id = :zonaId")
    List<TarifaCosto> findByZonaViajeId(@Param("zonaId") Long zonaId);

    @Query("SELECT t FROM TarifaCosto t JOIN FETCH t.adicionales WHERE t.zonaViaje = :zona")
    List<TarifaCosto> findByZonaViaje(@Param("zona") ZonaViaje zona);

    @EntityGraph(attributePaths = {"tipoVehiculo", "tipoCargaTarifa", "zonaViaje", "transportista"})
    Optional<TarifaCosto> findWithRelationsById(Long id);
}