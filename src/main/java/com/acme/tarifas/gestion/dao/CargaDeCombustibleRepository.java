package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CargaDeCombustibleRepository extends JpaRepository<CargaDeCombustible, Long> {

    
    List<CargaDeCombustible> findByVehiculoIdAndFechaBetweenAndEsVigenteTrue(
            String vehiculoId, 
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin);
    
    List<CargaDeCombustible> findByEsVigenteTrue();
}