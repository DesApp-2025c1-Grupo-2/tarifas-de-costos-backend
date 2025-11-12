// Archivo: src/main/java/com/acme/tarifas/gestion/dao/TarifaCostoHistorialRepository.java
package com.acme.tarifas.gestion.dao;

import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // Asegúrate de importar List

public interface TarifaCostoHistorialRepository extends JpaRepository<TarifaCostoHistorial, Long> {
    List<TarifaCostoHistorial> findByTarifaOriginalId(Long tarifaId);

    // --- NUEVO MÉTODO ---
    // Busca todos los registros de historial para un transportista específico,
    // ordenados por fecha de modificación descendente (más reciente primero).
    List<TarifaCostoHistorial> findByTransportistaIdOrderByFechaModificacionDesc(String transportistaId);
    // --- FIN NUEVO MÉTODO ---
}