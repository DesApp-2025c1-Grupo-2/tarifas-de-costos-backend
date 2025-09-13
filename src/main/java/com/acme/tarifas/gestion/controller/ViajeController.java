package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Viaje;
import com.acme.tarifas.gestion.service.ViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    @Autowired
    private ViajeService viajeService;

    /*
    @PostMapping
    public ResponseEntity<Viaje> registrarViaje(@RequestBody Viaje viaje) {
        Viaje nuevo = viajeService.registrarViaje(viaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }


    @GetMapping("/reporte-rentabilidad")
    public Map<String, Double> generarReporteRentabilidad(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {
        return viajeService.calcularRentabilidadPorPeriodo(inicio, fin);
    }
    */


}