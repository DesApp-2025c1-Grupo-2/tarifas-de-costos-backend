package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByTransportistaPropietarioId(Long transportistaId);
}