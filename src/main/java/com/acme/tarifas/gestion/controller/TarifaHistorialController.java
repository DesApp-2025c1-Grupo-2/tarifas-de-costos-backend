package com.acme.tarifas.gestion.controller;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import com.acme.tarifas.gestion.service.TarifaHistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class TarifaHistorialController {

    @Autowired
    private TarifaHistorialService historialService;

    @GetMapping("/tarifa/{tarifaId}")
    public ResponseEntity<List<TarifaCostoHistorial>> obtenerHistorialPorTarifa(@PathVariable Long tarifaId) {
        List<TarifaCostoHistorial> historial = historialService.obtenerHistorialTarifa(tarifaId);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}