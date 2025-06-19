package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import com.acme.tarifas.gestion.service.AdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/adicionales")
public class AdicionalController {

    @Autowired
    private AdicionalService adicionalService;

    @PostMapping
    public ResponseEntity<Adicional> crearAdicional(@RequestBody Adicional adicional) {
        Adicional nuevo = adicionalService.guardarAdicional(adicional);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public List<Adicional> obtenerTodosAdicionales() {
        return adicionalService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adicional> obtenerAdicionalPorId(@PathVariable Long id) {
        return adicionalService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/costo-default")
    public ResponseEntity<Adicional> actualizarCostoDefault(
            @PathVariable Long id,
            @RequestParam Double nuevoCosto) {
        return adicionalService.actualizarCostoDefault(id, nuevoCosto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAdicional(@PathVariable Long id) {
        if (adicionalService.eliminarAdicional(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<Adicional> baja(@PathVariable Long id) {
        try {
            Adicional adicional = adicionalService.baja(id);
            return ResponseEntity.ok(adicional);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}