package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO; 
import com.acme.tarifas.gestion.dto.VariacionTarifaDTO;
import com.acme.tarifas.gestion.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/frecuencia-adicionales")
    public ResponseEntity<List<FrecuenciaAdicionalDTO>> getFrecuenciaAdicionales() {
        List<FrecuenciaAdicionalDTO> datos = reporteService.getFrecuenciaUsoAdicionales();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/transportistas-mas-utilizados")
    public ResponseEntity<List<TransportistaTarifasDTO>> getTransportistasMasUtilizados() {
        List<TransportistaTarifasDTO> datos = reporteService.getTransportistasMasUtilizados();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/comparativa-aumentos")
    public ResponseEntity<List<VariacionTarifaDTO>> getComparativaAumentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<VariacionTarifaDTO> reporte = reporteService.generarComparativaTarifas(fechaInicio, fechaFin);
        
        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(reporte);
    }
}