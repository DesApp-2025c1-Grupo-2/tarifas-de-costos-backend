package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CargaDeCombustibleRepository extends JpaRepository<CargaDeCombustible, Long> {
    // Método para el reporte: buscar cargas por ID de vehículo y rango de fechas
    List<CargaDeCombustible> findByVehiculoIdAndFechaBetween(String vehiculoId, LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}