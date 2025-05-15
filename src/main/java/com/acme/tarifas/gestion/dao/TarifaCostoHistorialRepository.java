package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaCostoHistorialRepository extends JpaRepository<TarifaCostoHistorial, Long> {
    List<TarifaCostoHistorial> findByTarifaOriginalId(Long tarifaId);
}