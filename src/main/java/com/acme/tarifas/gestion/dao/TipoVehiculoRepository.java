package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {
}