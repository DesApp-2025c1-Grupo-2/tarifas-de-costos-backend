package com.acme.tarifas.gestion.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import java.util.List;

import java.util.List;

public interface TarifaAdicionalRepository extends JpaRepository<TarifaAdicional, Long> {

    List<TarifaAdicional> findByTarifaCostoId(Long tarifaCostoId);

    boolean existsByTarifaCostoIdAndAdicionalId(Long tarifaCostoId, Long adicionalId);


    void deleteByTarifaCostoId(Long tarifaCostoId);


    @Query("SELECT a.id, a.nombre, COUNT(ta) " +
            "FROM Adicional a LEFT JOIN TarifaAdicional ta ON a.id = ta.adicional.id " +
            "GROUP BY a.id, a.nombre")
    List<Object[]> contarUsoPorAdicional();

    @Query("SELECT ta FROM TarifaAdicional ta WHERE ta.adicional.id = :idAdicional")
    List<TarifaAdicional> findByAdicionalId(Long idAdicional);


}