package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TarifaAdicionalRepository extends JpaRepository<TarifaAdicional, Long> {

       List<TarifaAdicional> findByTarifaCostoId(Long tarifaCostoId);

       // --- ESTA ES LA MODIFICACIÓN CLAVE ---
       // Añadimos una consulta para borrar explícitamente las relaciones de
       // adicionales por el ID de la tarifa.
       @Modifying
       @Query("delete from TarifaAdicional ta where ta.tarifaCosto.id = ?1")
       void deleteByTarifaCostoId(Long tarifaCostoId);
       // --- FIN DE LA MODIFICACIÓN ---

       @Query("SELECT new com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO(ta.adicional.nombre, COUNT(ta)) " +
                     "FROM TarifaAdicional ta " +
                     "GROUP BY ta.adicional.nombre " +
                     "ORDER BY COUNT(ta) DESC")
       List<FrecuenciaAdicionalDTO> findFrecuenciaUsoAdicionales();
}