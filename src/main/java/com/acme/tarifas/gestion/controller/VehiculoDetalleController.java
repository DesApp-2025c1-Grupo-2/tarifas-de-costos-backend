package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.VehiculoDetalle;
import com.acme.tarifas.gestion.service.VehiculoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculo-detalles")
public class VehiculoDetalleController {

    @Autowired
    private VehiculoDetalleService service;

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<VehiculoDetalle> obtenerPorVehiculoId(@PathVariable String vehiculoId) {
        return service.obtenerPorVehiculoId(vehiculoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VehiculoDetalle guardarDetalle(@RequestBody VehiculoDetalle detalle) {
        return service.guardar(detalle);
    }
}