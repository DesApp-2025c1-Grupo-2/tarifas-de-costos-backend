package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarifaHistorialRepository extends JpaRepository<TarifaCostoHistorial, Long> {

    @Query("SELECT h FROM TarifaCostoHistorial h WHERE h.tarifaOriginal.id = :tarifaId ORDER BY h.fechaModificacion DESC")
    List<TarifaCostoHistorial> findByTarifaId(@Param("tarifaId") Long tarifaId);
}