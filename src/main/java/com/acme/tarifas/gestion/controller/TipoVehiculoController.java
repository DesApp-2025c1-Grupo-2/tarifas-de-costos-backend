package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    private ViajesClient viajesClient;

    public TipoVehiculoController(ViajesClient viajesClient){
        this.viajesClient = viajesClient;
    }

    @GetMapping
    public List<TipoVehiculoDTO> obtenerTodosTipos() {
        return viajesClient.getTiposVehiculo();
    }

    @GetMapping("/{id}")
    public TipoVehiculoDTO getTipoPorId(@PathVariable String id) {
        return viajesClient.getTiposVehiculoById(id);
    }



}