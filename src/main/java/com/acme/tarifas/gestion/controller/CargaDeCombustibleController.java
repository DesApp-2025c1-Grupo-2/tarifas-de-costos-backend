package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.service.CargaDeCombustibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // Importar
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // Importar
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/cargasDeCombustible")
public class CargaDeCombustibleController {

    @Autowired
    private CargaDeCombustibleService cargaService;

    @GetMapping
    public List<CargaDeCombustible> obtenerTodasLasCargas(
            // NUEVO PARÁMETRO
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio) {
        // Pasa el parámetro al servicio
        return cargaService.obtenerTodasLasCargas(fechaInicio);
    }

    @PostMapping
    // ... (el resto de la clase queda igual)
    // ...
    public ResponseEntity<CargaDeCombustible> crearCarga(@RequestBody CargaDeCombustible carga) {
        CargaDeCombustible nuevaCarga = cargaService.guardarCarga(carga);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCarga);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargaDeCombustible> actualizarCarga(@PathVariable Long id,
            @RequestBody CargaDeCombustible carga) {
        return cargaService.actualizarCarga(id, carga)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<Void> darBajaCarga(@PathVariable Long id) {
        cargaService.darBaja(id);
        return ResponseEntity.noContent().build();
    }
}