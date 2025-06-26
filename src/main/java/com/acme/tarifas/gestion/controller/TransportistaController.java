package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
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
        try {
            transportistaService.eliminarTransportista(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}/baja")
    public ResponseEntity<Transportista> baja(@PathVariable Long id) {
        try {
            Transportista transportista = transportistaService.baja(id);
            return ResponseEntity.ok(transportista);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/analisis-tarifas")
    public ResponseEntity<Map<String, Object>> obtenerAnalisisTarifasTransportista(@PathVariable Long id) {
        return ResponseEntity.ok(transportistaService.analizarTarifasTransportista(id));
    }

    @GetMapping("/comparativa-costos")
    public ResponseEntity<List<Map<String, Object>>> obtenerComparativaCostosTransportistas() {
        return ResponseEntity.ok(transportistaService.compararCostosTransportistas());
    }

    @GetMapping("/ranking-economicos")
    public ResponseEntity<List<Transportista>> obtenerRankingEconomicos() {
        return ResponseEntity.ok(transportistaService.obtenerTransportistasOrdenadosPorCostoPromedio());
    }

    @GetMapping("/relacion-precio-calidad")
    public ResponseEntity<List<Map<String, Object>>> analizarRelacionPrecioCalidad() {
        return ResponseEntity.ok(transportistaService.analizarRelacionPrecioCalidad());

    }
}