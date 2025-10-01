package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.TarifaCostoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaCostoController {

    @Autowired
    private TarifaCostoService tarifaService;

    @PostMapping
    public ResponseEntity<?> crearTarifa(@RequestBody TarifaCosto tarifa) {
        try {
            TarifaCosto nueva = tarifaService.crearTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifaCosto> actualizarTarifa(@PathVariable Long id, @RequestBody TarifaCosto tarifa) {
        return tarifaService.actualizarTarifa(id, tarifa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- INICIO DE LA MODIFICACIÓN ---
    @GetMapping
    public List<TarifaCostoDTO> obtenerTodasTarifas(
            @RequestParam(required = false) Long tipoVehiculo,
            @RequestParam(required = false) Long zona,
            @RequestParam(required = false) Long tipoCarga,
            @RequestParam(required = false) String transportista) { // El ID del transportista es String

        // Se llama al método optimizado del servicio
        return tarifaService.filtrarTarifas(
                tipoVehiculo,
                zona,
                tipoCarga,
                transportista);
    }
    // --- FIN DE LA MODIFICACIÓN ---

    @GetMapping("/{id}")
    public ResponseEntity<TarifaCostoDTO> getTarifaPorId(@PathVariable Long id) {
        return tarifaService.obtenerTarifaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/adicionales")
    public ResponseEntity<TarifaAdicional> agregarAdicional(
            @PathVariable Long id,
            @RequestBody TarifaAdicional adicional) {
        return tarifaService.agregarAdicional(id, adicional)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/valor-base")
    public ResponseEntity<TarifaCosto> actualizarValorBase(
            @PathVariable Long id,
            @RequestParam Double nuevoValor) {
        return tarifaService.actualizarValorBase(id, nuevoValor)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/adicionales")
    public ResponseEntity<List<TarifaAdicional>> getAdicionalesPorTarifa(@PathVariable Long id) {
        List<TarifaAdicional> adicionales = tarifaService.obtenerAdicionalesPorTarifa(id);
        if (adicionales.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adicionales);
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<Void> cambiarVigencia(@PathVariable Long id) {
        tarifaService.cambiarVigencia(id);
        return ResponseEntity.noContent().build();
    }
}