package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCargaTarifaRepository extends JpaRepository<TipoCargaTarifa, Long> {
    boolean existsByNombreAndActivoTrue(String nombre);
}