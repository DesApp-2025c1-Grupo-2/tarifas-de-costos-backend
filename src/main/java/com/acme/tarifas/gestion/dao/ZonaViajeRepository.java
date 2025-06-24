package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface ZonaViajeRepository extends JpaRepository<ZonaViaje, Long> {
    @Query("SELECT z FROM ZonaViaje z ORDER BY z.nombre ASC")
    List<ZonaViaje> findAllOrderedByNombre();
}