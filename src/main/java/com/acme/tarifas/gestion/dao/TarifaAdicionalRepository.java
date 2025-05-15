package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaAdicionalRepository extends JpaRepository<TarifaAdicional, Long> {
}