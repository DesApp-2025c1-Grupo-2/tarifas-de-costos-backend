package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/camiones")
public class VehiculoController {

    private ViajesClient viajesClient;

    public VehiculoController(ViajesClient viajesClient) {
        this.viajesClient = viajesClient;
    }

    @GetMapping
    public List<VehiculoDTO> getVehiculos() {
        return viajesClient.getVehiculos();
    }


    @GetMapping("/{id}")
    public VehiculoDTO getVehiculoById(@PathVariable String id){
        return viajesClient.getVehiculoById(id);
    }


}
