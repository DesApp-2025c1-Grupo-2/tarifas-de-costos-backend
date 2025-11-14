package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import com.acme.tarifas.gestion.service.TipoCargaTarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-carga-tarifa")
public class TipoCargaTarifaController {

    @Autowired
    TipoCargaTarifaService tipoCargaTarifaService;

    @GetMapping("/{id}")
    public ResponseEntity<TipoCargaTarifa> obtenerTipoCargaPorId(@PathVariable Long id) {
        return tipoCargaTarifaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TipoCargaTarifa> obtenerTodos() {
        return tipoCargaTarifaService.obtenerTodosTiposCargaTarifa();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTipoCargaTarifa(@PathVariable Long id) {
        try {
            tipoCargaTarifaService.eliminarTipoCargaTarifa(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TipoCargaTarifa> crearTipoCargaTarifa(@RequestBody TipoCargaTarifa tipo) {
        try {
            TipoCargaTarifa nuevo = tipoCargaTarifaService.guardarTipoCargaTarifa(tipo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<TipoCargaTarifa> baja(@PathVariable Long id) {
        try {
            TipoCargaTarifa tipoCarga = tipoCargaTarifaService.baja(id);
            return ResponseEntity.ok(tipoCarga);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCargaTarifa> actualizarTipo(@PathVariable Long id, @RequestBody TipoCargaTarifa tipo) {
        try {
            return tipoCargaTarifaService.actualizarTipo(id, tipo)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
