package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import com.acme.tarifas.gestion.entity.TipoVehiculo;
import com.acme.tarifas.gestion.service.TipoVehiculoService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    @Autowired
    private TipoVehiculoService tipoVehiculoService;

    private ViajesClient viajesClient;

    public TipoVehiculoController(ViajesClient viajesClient){
        this.viajesClient = viajesClient;
    }

    @GetMapping
    public List<JsonNode> obtenerTodosTipos() {
        return viajesClient.getTiposVehiculo();
    }

    @GetMapping("/{id}")
    public JsonNode getTipoPorId(@PathVariable String id) {
        return viajesClient.getTiposVehiculoById(id);
    }



}