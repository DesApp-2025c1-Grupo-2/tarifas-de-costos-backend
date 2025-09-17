package com.acme.tarifas.gestion.controller;
import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.service.TransportistaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    @Autowired
    private ViajesClient viajesClient;

    public TransportistaController(ViajesClient viajesClient) {
        this.viajesClient = viajesClient;
    }

    @GetMapping("/{id}/profile") //Corregir
    public ResponseEntity<TransportistaService.TransportistaProfile> getTransportistaProfile(@PathVariable Long id) {
        return transportistaService.getTransportistaProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<JsonNode>getTransportistas(){
        return viajesClient.getTransportistas();
    }

    /*
    con DTO
    @GetMapping
    public List<TransportistaDTO> obtenerTodosTransportistas() {
        return viajesClient.getTransportistas();
    } //Devuelve los transportistas de la api de viajes
    */


    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerTransportistaPorId(@PathVariable Long id) {
        return transportistaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


} 