package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.service.AdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}/costo-default")
    public ResponseEntity<Adicional> actualizarCostoDefault(
            @PathVariable Long id,
            @RequestParam Double nuevoCosto) {
        return adicionalService.actualizarCostoDefault(id, nuevoCosto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}