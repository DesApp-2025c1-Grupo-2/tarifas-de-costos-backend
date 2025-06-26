package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaAdicionalRepository extends JpaRepository<TarifaAdicional, Long> {
    List<TarifaAdicional> findByTarifaCostoId(Long tarifaCostoId);

    boolean existsByTarifaCostoIdAndAdicionalId(Long tarifaCostoId, Long adicionalId);

    void deleteByTarifaCostoId(Long tarifaCostoId);
}