package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
}