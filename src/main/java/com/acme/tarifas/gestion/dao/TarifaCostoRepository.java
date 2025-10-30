package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCosto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // Importar
import java.util.List; 

@Repository
public interface TarifaCostoRepository extends JpaRepository<TarifaCosto, Long> {

    List<TarifaCosto> findByEsVigenteTrue();

    // NUEVO MÉTODO
    List<TarifaCosto> findByEsVigenteTrueAndFechaCreacionAfter(LocalDateTime fechaInicio);

    @Query("SELECT t.transportistaId, COUNT(t) " +
            "FROM TarifaCosto t " +
//... (el resto de la clase queda igual)
//...
            "WHERE t.transportistaId IS NOT NULL " +
            "GROUP BY t.transportistaId " +
            "ORDER BY COUNT(t) DESC")
    List<Object[]> countByTransportista();
}