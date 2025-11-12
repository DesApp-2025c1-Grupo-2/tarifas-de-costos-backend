package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZonaViajeRepository extends JpaRepository<ZonaViaje, Long> {
    @EntityGraph(attributePaths = "provincias")
    List<ZonaViaje> findAll();

    boolean existsByNombreAndActivoTrue(String nombre);

    Optional<ZonaViaje> findByNombreAndActivoTrue(String nombre);
}