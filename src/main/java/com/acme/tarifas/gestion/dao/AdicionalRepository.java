package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
    @Query("SELECT a FROM Adicional a WHERE a.esGlobal = true")
    List<Adicional> findAllGlobales();

    boolean existsByNombreAndActivoTrue(String nombre);

    Optional<Adicional> findByNombreAndActivoTrue(String nombre);
}
