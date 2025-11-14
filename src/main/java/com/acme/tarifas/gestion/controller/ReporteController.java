package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.*;
// --- IMPORT ELIMINADO ---
// import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Asegúrate de que este import esté presente

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
    public ResponseEntity<List<FrecuenciaAdicionalDTO>> getFrecuenciaAdicionales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<FrecuenciaAdicionalDTO> datos = reporteService.getFrecuenciaUsoAdicionales(fechaInicio, fechaFin);

        if (datos.isEmpty()) {
            // Devolver 204 No Content si no hay datos que mostrar
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(datos);
    }

    // --- ENDPOINT ELIMINADO ---
    // @GetMapping("/transportistas-mas-utilizados")
    // public ResponseEntity<List<TransportistaTarifasDTO>>
    // getTransportistasMasUtilizados() {
    // List<TransportistaTarifasDTO> datos =
    // reporteService.getTransportistasMasUtilizados();
    // return ResponseEntity.ok(datos);
    // }

    @GetMapping("/uso-combustible")
    public ResponseEntity<ReporteVehiculoCombustibleDTO> getReporteUsoCombustible(
            @RequestParam String vehiculoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            ReporteVehiculoCombustibleDTO reporte = reporteService.generarReporteUsoCombustible(vehiculoId, fechaInicio,
                    fechaFin);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            System.err.println("Error generando reporte de combustible: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/comparativa-costos")
    public ResponseEntity<ComparativaTransportistaDTO> getComparativaCostos(
            @RequestParam(required = false) Long zonaId,
            @RequestParam(required = false) String tipoVehiculoId,
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