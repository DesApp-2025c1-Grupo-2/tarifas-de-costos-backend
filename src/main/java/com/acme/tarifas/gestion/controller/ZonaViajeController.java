package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import com.acme.tarifas.gestion.service.ZonaViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/zonas")
public class ZonaViajeController {

    @Autowired
    private ZonaViajeService zonaService;

    @GetMapping
    public List<ZonaViaje> obtenerTodasLasZonas() {
        return zonaService.getZonas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaViaje> obtenerZonaPorId(@PathVariable Long id) {
        return zonaService.getZonaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "nombre")
    public ResponseEntity<ZonaViaje> obtenerZonaPorNombre(
            @Parameter(description = "Nombre de la zona") @RequestParam String nombre) {
        return zonaService.getZonaByNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ZonaViaje> crearZona(@RequestBody ZonaViajeDTO zonaDto) {
        ZonaViaje zona = new ZonaViaje();
        zona.setNombre(zonaDto.getNombre());
        zona.setDescripcion(zonaDto.getDescripcion());
        zona.setRegionMapa(zonaDto.getRegionMapa());
        zona.setActivo(zonaDto.getActivo());
        ZonaViaje nuevaZona = zonaService.guardarZona(zona, zonaDto.getProvinciasNombres());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaZona);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarZona(@PathVariable Long id) {
        try {
            zonaService.eliminarZona(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/comparativa-costos")
    public Map<String, Object> compararCostosPorZona() {
        return zonaService.obtenerComparativaCostos();
    }

    @GetMapping("/{id}/tarifas")
    public List<TarifaCosto> obtenerTarifasPorZona(@PathVariable Long id) {
        return zonaService.obtenerTarifasZona(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZonaViaje> actualizarZona(@PathVariable Long id, @RequestBody ZonaViajeDTO zonaDto) {
        ZonaViaje zona = new ZonaViaje();
        zona.setNombre(zonaDto.getNombre());
        zona.setDescripcion(zonaDto.getDescripcion());
        zona.setRegionMapa(zonaDto.getRegionMapa());
        zona.setActivo(zonaDto.getActivo());
        return zonaService.actualizarZona(id, zona, zonaDto.getProvinciasNombres()) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<ZonaViaje> baja(@PathVariable Long id) {
        try {
            ZonaViaje zona = zonaService.baja(id);
            return ResponseEntity.ok(zona);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}