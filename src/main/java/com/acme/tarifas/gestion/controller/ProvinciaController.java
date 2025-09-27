package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Provincia;
import com.acme.tarifas.gestion.service.ProvinciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provincias")
public class ProvinciaController {

    @Autowired
    private ProvinciaService provinciaService;

    @GetMapping
    public List<Provincia> obtenerTodas() {
        return provinciaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Provincia> obtenerPorId(@PathVariable Long id) {
        return provinciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Provincia> crear(@RequestBody Provincia provincia) {
        Provincia nueva = provinciaService.guardar(provincia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}