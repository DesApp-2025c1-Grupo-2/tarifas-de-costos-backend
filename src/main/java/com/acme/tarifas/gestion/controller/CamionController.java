package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private ViajesClient viajesClient;

    public CamionController(ViajesClient viajesClient) {
        this.viajesClient = viajesClient;
    }

    @GetMapping
    public List<JsonNode> getCamiones() {
        return viajesClient.getCamiones();
    }


    @GetMapping("/{id}")
    public JsonNode getCamionById(@PathVariable String id){
        return viajesClient.getCamionById(id);
    }


}
