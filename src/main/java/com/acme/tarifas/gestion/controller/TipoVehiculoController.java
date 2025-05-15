package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TipoVehiculo;
import com.acme.tarifas.gestion.service.TipoVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    @Autowired
    private TipoVehiculoService tipoVehiculoService;

    @PostMapping
    public ResponseEntity<TipoVehiculo> crearTipoVehiculo(@RequestBody TipoVehiculo tipo) {
        TipoVehiculo nuevo = tipoVehiculoService.guardarTipo(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public List<TipoVehiculo> obtenerTodosTipos() {
        return tipoVehiculoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculo> obtenerTipoPorId(@PathVariable Long id) {
        return tipoVehiculoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}