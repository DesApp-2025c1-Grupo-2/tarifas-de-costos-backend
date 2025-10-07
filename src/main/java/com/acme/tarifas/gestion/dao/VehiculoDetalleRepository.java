package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.VehiculoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehiculoDetalleRepository extends JpaRepository<VehiculoDetalle, Long> {
    Optional<VehiculoDetalle> findByVehiculoId(String vehiculoId);
}