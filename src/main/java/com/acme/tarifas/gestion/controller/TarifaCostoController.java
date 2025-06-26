package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.TarifaCostoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tarifas")
public class TarifaCostoController {

    private final TarifaCostoService tarifaService;

    @Autowired
    public TarifaCostoController(TarifaCostoService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @PostMapping
    public ResponseEntity<?> crearTarifa(@RequestBody TarifaCosto tarifa) {
        try {
            TarifaCosto nueva = tarifaService.crearTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<TarifaCostoDTO> obtenerTodasTarifas(

    

            @RequestParam(required = false) Long tipoVehiculo,
            @RequestParam(required = false) Long zona,
            @RequestParam(required = false) Long tipoCarga,
            @RequestParam(required = false) Long transportista){


        return tarifaService.filtrarTarifas(tipoVehiculo, zona, tipoCarga,transportista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaCostoDTO> getTarifaPorId(@PathVariable Long id) {
        return tarifaService.obtenerTarifaPorId(id);
    }



    @PostMapping("/{id}/adicionales")
    public ResponseEntity<?> agregarAdicional(
            @PathVariable Long id,
            @RequestParam Long adicionalId,
            @RequestParam(required = false) Double costoEspecifico) {
        try {
            TarifaAdicional nuevo = tarifaService.agregarAdicional(id, adicionalId, costoEspecifico);
            return ResponseEntity.ok(nuevo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/valor-base")
    public ResponseEntity<?> actualizarValorBase(
            @PathVariable Long id,
            @RequestParam Double nuevoValor) {
        try {
            TarifaCosto actualizada = tarifaService.actualizarValorBase(id, nuevoValor);
            return ResponseEntity.ok(actualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<?> calcularTotalTarifa(@PathVariable Long id) {
        try {
            double total = tarifaService.calcularTotalTarifa(id);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/adicionales")
    public ResponseEntity<List<TarifaAdicional>>getAdicionalesPorTarifa(@PathVariable Long id){
        List<TarifaAdicional> adicionales = tarifaService.obtenerAdicionalesPorTarifa(id);
        if(adicionales.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adicionales);
    }

    @PutMapping("/{id}/vigencia")
    public ResponseEntity<Void> cambiarVigencia(@PathVariable Long id) {
        tarifaService.cambiarVigencia(id);
        return ResponseEntity.noContent().build();
    }
}