package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.service.CargaDeCombustibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cargasDeCombustible")
public class CargaDeCombustibleController {

    @Autowired
    private CargaDeCombustibleService cargaDeCombustibleService;

    @GetMapping
    public List<CargaDeCombustible> obtenerTodas() {
        return cargaDeCombustibleService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargaDeCombustible> obtenerPorId(@PathVariable Long id) {
        return cargaDeCombustibleService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CargaDeCombustible> crear(@RequestBody CargaDeCombustible carga) {
        CargaDeCombustible nueva = cargaDeCombustibleService.guardar(carga);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargaDeCombustible> actualizar(@PathVariable Long id, @RequestBody CargaDeCombustible carga) {
        return cargaDeCombustibleService.actualizar(id, carga)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<CargaDeCombustible> baja(@PathVariable Long id) {
        try {
            CargaDeCombustible carga = cargaDeCombustibleService.baja(id);
            return ResponseEntity.ok(carga);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}