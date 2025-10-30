// Archivo: src/main/java/com/acme/tarifas/gestion/controller/TarifaHistorialController.java
package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.TarifaCostoHistorialDTO; // Importar DTO
// Quitar import de la entidad si ya no se usa directamente
// import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import com.acme.tarifas.gestion.service.TarifaHistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class TarifaHistorialController {

    @Autowired
    private TarifaHistorialService historialService;

    @GetMapping("/tarifa/{tarifaId}")
    // Cambiar el tipo de retorno a List<TarifaCostoHistorialDTO>
    public ResponseEntity<List<TarifaCostoHistorialDTO>> obtenerHistorialPorTarifa(@PathVariable Long tarifaId) {
        List<TarifaCostoHistorialDTO> historialDTOs = historialService.obtenerHistorialTarifa(tarifaId); // Obtener DTOs
        if (historialDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historialDTOs); // Devolver DTOs
    }
}