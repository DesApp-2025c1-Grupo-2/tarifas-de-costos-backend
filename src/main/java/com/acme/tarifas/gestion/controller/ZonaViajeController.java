package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.ZonaViajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zonas")
public class ZonaViajeController {

    @Autowired
    private ZonaViajeService zonaService;

    @GetMapping("/comparativa-costos")
    public Map<String, Object> compararCostosPorZona() {
        return zonaService.obtenerComparativaCostos();
    }

    @GetMapping("/{id}/tarifas")
    public List<TarifaCosto> obtenerTarifasPorZona(@PathVariable Long id) {
        return zonaService.obtenerTarifasZona(id);
    }
}