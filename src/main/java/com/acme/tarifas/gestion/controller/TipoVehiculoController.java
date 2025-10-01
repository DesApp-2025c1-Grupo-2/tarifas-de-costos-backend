package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoViewDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoFormDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    private ViajesClient viajesClient;

    public TipoVehiculoController(ViajesClient viajesClient) {
        this.viajesClient = viajesClient;
    }

    @GetMapping("/form")
    public List<TipoVehiculoFormDTO> obtenerParaFormulario() {
        return viajesClient.getTiposVehiculo().stream()
                .map(TipoVehiculoFormDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<TipoVehiculoViewDTO> obtenerTodosTipos() {
        return viajesClient.getVehiculos().stream()
                .map(TipoVehiculoViewDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TipoVehiculoDTO getTipoPorId(@PathVariable String id) {
        return viajesClient.getTiposVehiculoById(id);
    }
}