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
        // // Si la lista está vacía, podrías devolver 204 No Content o un OK con lista
        // vacía.
        // // OK con lista vacía es común en APIs REST.
        // return ResponseEntity.ok(datos);
        // }
        // --- FIN ---

        @GetMapping("/uso-combustible")
        public ResponseEntity<ReporteVehiculoCombustibleDTO> getReporteUsoCombustible(
                        @RequestParam String vehiculoId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

                try {
                        ReporteVehiculoCombustibleDTO reporte = reporteService.generarReporteUsoCombustible(vehiculoId,
                                        fechaInicio, fechaFin);
                        // Considerar si realmente quieres devolver 204 si ambos son 0, o si un reporte
                        // con 0s es válido.
                        // Aquí se devuelve OK incluso si los conteos son 0.
                        return ResponseEntity.ok(reporte);
                } catch (Exception e) {
                        // Podrías loguear el error y devolver un 500 Internal Server Error
                        System.err.println("Error generando reporte de combustible: " + e.getMessage());
                        // Devolver 404 si el error fue porque no se encontró el vehículo podría ser una
                        // opción,
                        // pero requeriría capturar una excepción específica si ViajesClient la lanza.
                        // Por ahora, un 500 genérico si algo falla.
                        return ResponseEntity.internalServerError().build();
                }
        }

        @GetMapping("/comparativa-costos")
        public ResponseEntity<ComparativaTransportistaDTO> getComparativaCostos(
                        @RequestParam(required = false) Long zonaId,
                        // ----- CORRECCIÓN APLICADA AQUÍ -----
                        @RequestParam(required = false) String tipoVehiculoId, // Cambiado de Long a String
                        // ------------------------------------
                        @RequestParam(required = false) Long tipoCargaId) {

                ComparativaTransportistaDTO reporte = reporteService.generarComparativaPorServicio(zonaId,
                                tipoVehiculoId, tipoCargaId);

                if (reporte.getComparativas() == null || reporte.getComparativas().isEmpty()) {
                        // Devolver 204 No Content es apropiado aquí si no se encuentran tarifas que
                        // coincidan
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
                        // Devolver 204 No Content si no hay variaciones en el período
                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(reporte);
        }

}