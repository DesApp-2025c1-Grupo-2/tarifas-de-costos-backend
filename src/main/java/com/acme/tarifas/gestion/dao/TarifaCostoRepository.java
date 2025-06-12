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

    @Query("SELECT AVG(t.valorBase), MIN(t.valorBase), MAX(t.valorBase), COUNT(t) " +
            "FROM TarifaCosto t " +
            "WHERE t.transportista.id = :idTransportista AND t.esVigente = true")
    List<Object[]> obtenerEstadisticasTarifasPorTransportista(@Param("idTransportista") Long idTransportista);

    @Query("SELECT t.transportista.id, t.transportista.nombreEmpresa, " +
            "AVG(t.valorBase), MIN(t.valorBase), MAX(t.valorBase), COUNT(t) " +
            "FROM TarifaCosto t " +
            "WHERE t.esVigente = true " +
            "GROUP BY t.transportista.id, t.transportista.nombreEmpresa")
    List<Object[]> obtenerComparativaCostosTransportistas();

    @Query("SELECT t.transportista.id " +
            "FROM TarifaCosto t " +
            "WHERE t.esVigente = true " +
            "GROUP BY t.transportista.id " +
            "ORDER BY AVG(t.valorBase)")
    List<Long> obtenerIdsTransportistasOrdenadosPorCostoPromedio();

    @Query("SELECT t.transportista.id, t.transportista.nombreEmpresa, " +
            "t.transportista.evaluacionDesempeno, AVG(t.valorBase) " +
            "FROM TarifaCosto t " +
            "WHERE t.esVigente = true " +
            "GROUP BY t.transportista.id, t.transportista.nombreEmpresa, t.transportista.evaluacionDesempeno")
    List<Object[]> obtenerDatosParaRelacionPrecioCalidad();

    @Query("SELECT t FROM TarifaCosto t WHERE t.transportista.id = :idTransportista AND t.esVigente = true")
    List<TarifaCosto> findByTransportistaId(@Param("idTransportista") Long idTransportista);

    @Query("SELECT t.tipoVehiculo.id, t.tipoVehiculo.nombre, AVG(t.valorBase) " +
            "FROM TarifaCosto t " +
            "WHERE t.transportista.id = :idTransportista AND t.esVigente = true " +
            "GROUP BY t.tipoVehiculo.id, t.tipoVehiculo.nombre")
    List<Object[]> obtenerCostosPromedioPorTipoVehiculo(@Param("idTransportista") Long idTransportista);
}