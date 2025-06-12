package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.TarifaCostoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tarifas")
public class TarifaCostoController {

    @Autowired
    private TarifaCostoService tarifaService;

    @PostMapping
    public ResponseEntity<TarifaCosto> crearTarifa(@RequestBody TarifaCosto tarifa) {
        TarifaCosto nueva = tarifaService.crearTarifa(tarifa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping
    public List<TarifaCosto> obtenerTodasTarifas(
            @RequestParam(required = false) Long tipoVehiculo,
            @RequestParam(required = false) Long zona,
            @RequestParam(required = false) Long transportista) {

        return tarifaService.filtrarTarifas(tipoVehiculo, zona, transportista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaCosto> obtenerTarifaPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorId(id)
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
}