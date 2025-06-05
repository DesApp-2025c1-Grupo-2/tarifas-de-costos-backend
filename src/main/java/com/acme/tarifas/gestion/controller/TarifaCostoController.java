package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.TarifaCostoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaCostoController {

    private final TarifaCostoService tarifaService;

    @Autowired
    public TarifaCostoController(TarifaCostoService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @PostMapping
    public ResponseEntity<?> crearTarifa(@RequestBody TarifaCosto tarifa) {
        try {
            TarifaCosto nueva = tarifaService.crearTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<TarifaCosto>> obtenerTodasTarifas(
            @RequestParam(required = false) Long tipoVehiculo,
            @RequestParam(required = false) Long zona,
            @RequestParam(required = false) Long transportista) {

        List<TarifaCosto> tarifas = tarifaService.filtrarTarifas(tipoVehiculo, zona, transportista);
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTarifaPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorIdConAdicionales(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/adicionales")
    public ResponseEntity<?> agregarAdicional(
            @PathVariable Long id,
            @RequestParam Long adicionalId,
            @RequestParam(required = false) Double costoEspecifico) {
        try {
            TarifaAdicional nuevo = tarifaService.agregarAdicional(id, adicionalId, costoEspecifico);
            return ResponseEntity.ok(nuevo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/valor-base")
    public ResponseEntity<?> actualizarValorBase(
            @PathVariable Long id,
            @RequestParam Double nuevoValor) {
        try {
            TarifaCosto actualizada = tarifaService.actualizarValorBase(id, nuevoValor);
            return ResponseEntity.ok(actualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<?> calcularTotalTarifa(@PathVariable Long id) {
        try {
            double total = tarifaService.calcularTotalTarifa(id);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}