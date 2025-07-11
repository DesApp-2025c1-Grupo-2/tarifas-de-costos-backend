package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {
    boolean existsByNombreAndActivoTrue(String nombre);

    Optional<TipoVehiculo> findByNombreAndActivoTrue(String nombre);
}