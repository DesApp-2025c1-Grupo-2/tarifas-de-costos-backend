package com.acme.tarifas.gestion.controller;
import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.service.TransportistaService;
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

    @GetMapping
    public List<TransportistaDTO>getTransportistas(){
        return viajesClient.getTransportistas();
    }


    @GetMapping("/{id}")
    public TransportistaDTO getTransportistaPorId(@PathVariable String id) {
        return viajesClient.getTransportistaById(id);
    }



    @GetMapping("/{id}/profile")
    public ResponseEntity<TransportistaService.TransportistaProfile> getTransportistaProfile(@PathVariable String id) {
        return transportistaService.getTransportistaProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



} 