package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.ZonaViajeDTO; // Importa y USA el DTO
import com.acme.tarifas.gestion.entity.TarifaCosto; // Mantén si obtenerTarifasPorZona devuelve entidad
import com.acme.tarifas.gestion.service.ZonaViajeService;
import jakarta.persistence.EntityNotFoundException; // Para capturar excepciones específicas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Para manejar el Optional devuelto por el servicio

@RestController
@RequestMapping("/api/zonas")
public class ZonaViajeController {

    @Autowired
    private ZonaViajeService zonaService;

    @GetMapping
    public List<ZonaViajeDTO> obtenerTodasLasZonas() {
        // Llama al método del servicio que devuelve DTOs
        return zonaService.getZonasDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaViajeDTO> obtenerZonaPorId(@PathVariable Long id) {
        try {
            // Llama al método del servicio que devuelve DTO
            ZonaViajeDTO dto = zonaService.getZonaDTOById(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            // Si el servicio lanza EntityNotFoundException, devolvemos 404
            return ResponseEntity.notFound().build();
        }
        // Considera capturar otras excepciones si es necesario
    }

    @GetMapping(params = "nombre")
    public ResponseEntity<ZonaViajeDTO> obtenerZonaPorNombre(
            @Parameter(description = "Nombre de la zona") @RequestParam String nombre) {
        // Llama al método del servicio que devuelve Optional<DTO>
        Optional<ZonaViajeDTO> dtoOpt = zonaService.getZonaDTOByNombre(nombre);
        return dtoOpt.map(ResponseEntity::ok) // Si el Optional contiene un DTO, devuelve 200 OK con el DTO
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si el Optional está vacío, devuelve 404 Not
                                                                     // Found
    }

    @PostMapping
    public ResponseEntity<?> crearZona(@RequestBody ZonaViajeDTO zonaDto) {
        try {
            // Llama al método del servicio que recibe y devuelve DTO
            ZonaViajeDTO nuevaZonaDTO = zonaService.guardarZonaYDevolverDTO(zonaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaZonaDTO);
        } catch (IllegalArgumentException e) {
            // Captura errores de validación (ej. nombre duplicado)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Captura otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear la zona"));
        }
    }

    // Este endpoint elimina físicamente. Si prefieres baja lógica, usa el endpoint
    // /baja
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZona(@PathVariable Long id) {
        try {
            zonaService.eliminarZona(id);
            return ResponseEntity.ok().build(); // O ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Si la zona no existe para eliminar
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint de Comparativa (ya devolvía Map, no necesita cambio de tipo)
    @GetMapping("/comparativa-costos")
    public Map<String, Object> compararCostosPorZona(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return zonaService.obtenerComparativaCostos(fechaInicio, fechaFin);
    }

    // Este endpoint podría necesitar devolver TarifaCostoDTO si TarifaCosto tiene
    // relaciones lazy
    @GetMapping("/{id}/tarifas")
    public ResponseEntity<List<TarifaCosto>> obtenerTarifasPorZona(@PathVariable Long id) {
        try {
            // Verificar si la zona existe primero (opcional pero bueno)
            zonaService.getZonaDTOById(id); // Lanza EntityNotFound si no existe
            List<TarifaCosto> tarifas = zonaService.obtenerTarifasZona(id);
            // ¡CUIDADO! Si TarifaCosto tiene relaciones LAZY, esto puede fallar aquí al
            // serializar.
            // Sería mejor que obtenerTarifasZona devuelva List<TarifaCostoDTO>.
            if (tarifas.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 si no hay tarifas para esa zona
            }
            return ResponseEntity.ok(tarifas);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 si la zona no existe
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarZona(@PathVariable Long id, @RequestBody ZonaViajeDTO zonaDto) {
        try {
            // Llama al método del servicio que devuelve Optional<DTO>
            Optional<ZonaViajeDTO> dtoActualizadoOpt = zonaService.actualizarZonaYDevolverDTO(id, zonaDto);
            return dtoActualizadoOpt
                    .map(ResponseEntity::ok) // Si se actualizó, devuelve 200 OK con el DTO
                    .orElseGet(() -> ResponseEntity.notFound().build()); // Si no se encontró la zona, devuelve 404
        } catch (IllegalArgumentException e) {
            // Captura errores de validación (ej. nombre duplicado)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Captura otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar la zona"));
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> baja(@PathVariable Long id) { // Cambiado el retorno a ResponseEntity<?> para manejar
                                                           // errores
        try {
            // Llama al método del servicio que devuelve DTO
            ZonaViajeDTO zonaDto = zonaService.bajaYDevolverDTO(id);
            return ResponseEntity.ok(zonaDto); // Devuelve 200 OK con el DTO de la zona dada de baja
        } catch (Exception e) {
            // Captura la excepción si la zona no existe o ya está inactiva
            // Usa ResponseStatusException o devuelve ResponseEntity directamente
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",
            // e.getMessage()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}