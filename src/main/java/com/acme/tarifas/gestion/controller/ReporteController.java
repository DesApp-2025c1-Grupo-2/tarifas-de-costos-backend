package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.*;
import com.acme.tarifas.gestion.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/historial-servicios-transportista/{transportistaId}")
    public ResponseEntity<List<HistorialServicioDTO>> getHistorialServiciosTransportista(@PathVariable Long transportistaId) {
        List<HistorialServicioDTO> historial = reporteService.generarHistorialServiciosTransportista(transportistaId);
        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
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

    @GetMapping("/comparativa-costos")
    public ResponseEntity<ComparativaTransportistaDTO> getComparativaCostos(
            @RequestParam(required = false) Long zonaId,
            @RequestParam(required = false) Long tipoVehiculoId,
            @RequestParam(required = false) Long tipoCargaId) {

        ComparativaTransportistaDTO reporte = reporteService.generarComparativaPorServicio(zonaId, tipoVehiculoId,
                tipoCargaId);

        if (reporte.getComparativas() == null || reporte.getComparativas().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reporte);
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