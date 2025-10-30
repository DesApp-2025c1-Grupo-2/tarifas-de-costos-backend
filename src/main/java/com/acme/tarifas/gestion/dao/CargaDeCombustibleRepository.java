package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // Importar
import java.util.List;

@Repository
public interface CargaDeCombustibleRepository extends JpaRepository<CargaDeCombustible, Long> {

    // Método modificado para ordenar
    List<CargaDeCombustible> findByEsVigenteTrueOrderByFechaDesc();

    // NUEVO MÉTODO
    List<CargaDeCombustible> findByEsVigenteTrueAndFechaAfterOrderByFechaDesc(LocalDateTime fechaInicio);
}