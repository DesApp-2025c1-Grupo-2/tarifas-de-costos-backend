package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.ZonaViajeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // Import quitado si no se usa en crearZona
import java.util.Optional;

@RestController
@RequestMapping("/api/zonas")
public class ZonaViajeController {

    @Autowired
    private ZonaViajeService zonaService;

    @GetMapping
    public List<ZonaViajeDTO> obtenerTodasLasZonas() {
        return zonaService.getZonasDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaViajeDTO> obtenerZonaPorId(@PathVariable Long id) {
        try {
            ZonaViajeDTO dto = zonaService.getZonaDTOById(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(params = "nombre")
    public ResponseEntity<ZonaViajeDTO> obtenerZonaPorNombre(
            @Parameter(description = "Nombre de la zona") @RequestParam String nombre) {
        Optional<ZonaViajeDTO> dtoOpt = zonaService.getZonaDTOByNombre(nombre);
        return dtoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearZona(@RequestBody ZonaViajeDTO zonaDto) {
        try {
            ZonaViajeDTO nuevaZonaDTO = zonaService.guardarZonaYDevolverDTO(zonaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaZonaDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear la zona"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZona(@PathVariable Long id) {
        try {
            zonaService.eliminarZona(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ENDPOINT ELIMINADO ---
    // @GetMapping("/comparativa-costos")
    // public Map<String, Object> compararCostosPorZona( ... ) { ... }
    // --- FIN DE LA MODIFICACIÓN ---

    @GetMapping("/{id}/tarifas")
    public ResponseEntity<List<TarifaCosto>> obtenerTarifasPorZona(@PathVariable Long id) {
        try {
            zonaService.getZonaDTOById(id);
            List<TarifaCosto> tarifas = zonaService.obtenerTarifasZona(id);
            if (tarifas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(tarifas);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarZona(@PathVariable Long id, @RequestBody ZonaViajeDTO zonaDto) {
        try {
            Optional<ZonaViajeDTO> dtoActualizadoOpt = zonaService.actualizarZonaYDevolverDTO(id, zonaDto);
            return dtoActualizadoOpt
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar la zona"));
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> baja(@PathVariable Long id) {
        try {
            ZonaViajeDTO zonaDto = zonaService.bajaYDevolverDTO(id);
            return ResponseEntity.ok(zonaDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}