package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.dto.TransportistaViewDTO;
import com.acme.tarifas.gestion.dto.TransportistaFormDTO;
import com.acme.tarifas.gestion.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    private final ViajesClient viajesClient;

    @Autowired
    public TransportistaController(ViajesClient viajesClient, TransportistaService transportistaService) {
        this.viajesClient = viajesClient;
        this.transportistaService = transportistaService;
    }

    @GetMapping("/form")
    public List<TransportistaFormDTO> obtenerParaFormulario() {
        return viajesClient.getTransportistas().stream()
                .map(TransportistaFormDTO::new)
                .collect(Collectors.toList());
    }

    // --- INICIO DE LA MODIFICACIÓN ---
    // Se ajusta este método para asegurar la eficiencia.
    @GetMapping
    public List<TransportistaViewDTO> getTransportistas() {
        return viajesClient.getTransportistas().stream()
                .map(TransportistaViewDTO::new)
                .collect(Collectors.toList());
    }
    // --- FIN DE LA MODIFICACIÓN ---

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