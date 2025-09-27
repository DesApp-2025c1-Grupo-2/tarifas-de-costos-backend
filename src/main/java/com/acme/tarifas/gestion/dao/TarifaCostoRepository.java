package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCosto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List; 

@Repository
public interface TarifaCostoRepository extends JpaRepository<TarifaCosto, Long> {


    List<TarifaCosto> findByEsVigenteTrue();

    @Query("SELECT t.transportistaId, COUNT(t) " +
            "FROM TarifaCosto t " +
            "WHERE t.transportistaId IS NOT NULL " +
            "GROUP BY t.transportistaId " +
            "ORDER BY COUNT(t) DESC")
    List<Object[]> countByTransportista();
}