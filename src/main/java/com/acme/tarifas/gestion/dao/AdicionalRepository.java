package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
}