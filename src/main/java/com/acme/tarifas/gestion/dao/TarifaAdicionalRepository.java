package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TarifaAdicionalRepository extends JpaRepository<TarifaAdicional, Long> {

       List<TarifaAdicional> findByTarifaCostoId(Long tarifaCostoId);


       @Query("SELECT new com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO(ta.adicional.nombre, COUNT(ta)) " +
                     "FROM TarifaAdicional ta " +
                     "GROUP BY ta.adicional.nombre " +
                     "ORDER BY COUNT(ta) DESC")
       List<FrecuenciaAdicionalDTO> findFrecuenciaUsoAdicionales();
}