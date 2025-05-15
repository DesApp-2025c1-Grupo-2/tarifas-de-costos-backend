package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    @PostMapping
    public ResponseEntity<Transportista> crearTransportista(@RequestBody Transportista transportista) {
        Transportista nuevo = transportistaService.guardarTransportista(transportista);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public List<Transportista> obtenerTodosTransportistas() {
        return transportistaService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerTransportistaPorId(@PathVariable Long id) {
        return transportistaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> actualizarTransportista(@PathVariable Long id, @RequestBody Transportista transportista) {
        return transportistaService.actualizarTransportista(id, transportista)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTransportista(@PathVariable Long id) {
        transportistaService.eliminarTransportista(id);
        return ResponseEntity.noContent().build();
    }
}