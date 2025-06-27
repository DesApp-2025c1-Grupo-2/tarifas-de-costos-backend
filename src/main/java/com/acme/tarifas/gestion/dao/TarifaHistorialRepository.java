package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaHistorialRepository extends JpaRepository<TarifaCostoHistorial, Long> {
}