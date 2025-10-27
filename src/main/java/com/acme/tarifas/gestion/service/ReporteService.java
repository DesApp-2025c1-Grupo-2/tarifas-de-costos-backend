// Archivo: src/main/java/com/acme/tarifas/gestion/service/ReporteService.java
package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.CargaDeCombustibleRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dto.*;
import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import com.acme.tarifas.gestion.entity.ZonaViaje; // Necesario para ComparativaTransportistaDTO
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final TarifaCostoHistorialRepository historialRepository;
    private final TarifaAdicionalRepository tarifaAdicionalRepository;
    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajesClient viajesClient;
    private final CargaDeCombustibleRepository cargaDeCombustibleRepository;

    // Estado esperado para filtrar los viajes finalizados (ignora mayúsculas/minúsculas)
    private static final String ESTADO_VIAJE_FINALIZADO = "fin de viaje";


    @Autowired
    public ReporteService(TarifaCostoHistorialRepository historialRepository,
            TarifaAdicionalRepository tarifaAdicionalRepository,
            TarifaCostoRepository tarifaCostoRepository,
            ViajesClient viajesClient,
            CargaDeCombustibleRepository cargaDeCombustibleRepository) {
        this.historialRepository = historialRepository;
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.tarifaCostoRepository = tarifaCostoRepository;
        this.viajesClient = viajesClient;
        this.cargaDeCombustibleRepository = cargaDeCombustibleRepository;
    }

    @Transactional(readOnly = true)
    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        LocalDateTime fin = null;
        if (fechaFin != null) {
            // Aseguramos que el filtro de fecha final incluya todo el día
            fin = fechaFin.atTime(23, 59, 59);
        }

        // Si no hay filtros de fecha, usamos el método original (sin fechas)
        if (inicio == null && fin == null) {
            return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
        }

        // Si hay filtros, usamos el método con join y parámetros
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionalesByFechaCreacion(inicio, fin);
    }

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        List<Object[]> rawResults = tarifaCostoRepository.countByTransportista();

        // Fetch all transporters once to avoid multiple API calls inside the loop
        Map<String, TransportistaDTO> transportistasMap = viajesClient.getTransportistas().stream()
                .collect(Collectors.toMap(TransportistaDTO::getId, Function.identity(), (existing, replacement) -> existing)); // Handle potential duplicates if any

        return rawResults.stream()
                .map(obj -> {
                    String transportistaId = (String) obj[0];
                    Long cantidad = (Long) obj[1];

                    // Look up in the map
                    TransportistaDTO dto = transportistasMap.get(transportistaId);
                    String nombreComercial = (dto != null && dto.getNombreComercial() != null) ? dto.getNombreComercial() : "Desconocido (ID: " + transportistaId + ")";

                    return new TransportistaTarifasDTO(nombreComercial, cantidad);
                })
                .toList();
    }


    public ComparativaTransportistaDTO generarComparativaPorServicio(Long zonaId, String tipoVehiculoId, Long tipoCargaId) { // Changed tipoVehiculoId to String
        List<TarifaCosto> tarifasCoincidentes = tarifaCostoRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoVehiculoId == null || (t.getTipoVehiculoId() != null
                        && t.getTipoVehiculoId().equals(tipoVehiculoId))) // Direct String comparison
                .filter(t -> tipoCargaId == null
                        || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
                .collect(Collectors.toList());

        // Fetch all transportistas and create a map for quick lookup
        Map<String, TransportistaDTO> transportistasMap = viajesClient.getTransportistas().stream()
                .collect(Collectors.toMap(TransportistaDTO::getId, Function.identity()));

        ComparativaTransportistaDTO comparativaDTO = new ComparativaTransportistaDTO();
        comparativaDTO.setServicio("Comparativa para la selección actual");

        List<ComparativaTransportistaDTO.Comparativa> comparativas = tarifasCoincidentes.stream()
                .filter(t -> t.getTransportistaId() != null && transportistasMap.containsKey(t.getTransportistaId()))
                .map(t -> {
                    TransportistaDTO transportista = transportistasMap.get(t.getTransportistaId());
                    ComparativaTransportistaDTO.Comparativa c = new ComparativaTransportistaDTO.Comparativa();
                    // Use getNombreComercial which exists in TransportistaDTO
                    c.setTransportista(transportista.getNombreComercial() != null ? transportista.getNombreComercial() : "Nombre no disponible");
                    c.setCosto(t.getValorTotal());
                    return c;
                })
                .sorted(Comparator.comparing(ComparativaTransportistaDTO.Comparativa::getCosto))
                .collect(Collectors.toList());

        comparativaDTO.setComparativas(comparativas);
        return comparativaDTO;
    }


    public List<VariacionTarifaDTO> generarComparativaTarifas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para la comparación.");
        }

        List<TarifaCostoHistorial> historiales = historialRepository.findAll().stream()
                .filter(h -> h.getFechaModificacion() != null && !h.getFechaModificacion().isBefore(inicio) && !h.getFechaModificacion().isAfter(fin))
                .sorted(Comparator.comparing(TarifaCostoHistorial::getFechaModificacion))
                .collect(Collectors.toList());

        Map<Long, List<TarifaCostoHistorial>> historialesPorTarifa = historiales.stream()
                .filter(h -> h.getTarifaOriginal() != null)
                .collect(Collectors.groupingBy(h -> h.getTarifaOriginal().getId()));

        List<VariacionTarifaDTO> reporte = new ArrayList<>();

        for (Map.Entry<Long, List<TarifaCostoHistorial>> entry : historialesPorTarifa.entrySet()) {
            List<TarifaCostoHistorial> registros = entry.getValue();
            if (registros.isEmpty()) continue;

            Optional<TarifaCosto> tarifaActualOpt = tarifaCostoRepository.findById(entry.getKey());
            // Considera incluir tarifas no vigentes si quieres ver su historial de aumento antes de ser dadas de baja
            if (tarifaActualOpt.isEmpty() /*|| !tarifaActualOpt.get().isEsVigente()*/) {
                continue;
            }

            TarifaCosto tarifaActual = tarifaActualOpt.get();
            TarifaCostoHistorial registroInicial = registros.get(0);
             // El valor final ahora es el valor BASE actual de la tarifa, no del último historial
            Double valorFinalActual = tarifaActual.getValorBase();
            // El valor inicial es el del primer registro DENTRO DEL RANGO
            Double valorInicialRango = registroInicial.getValorBase();

            // Si el valor inicial o final no existe (puede pasar si la tarifa se creó antes/después del rango
            // o si el valor base es nulo), se omite esta tarifa para evitar errores de cálculo.
             if (valorInicialRango == null || valorFinalActual == null) {
                continue;
            }


            VariacionTarifaDTO variacionDTO = new VariacionTarifaDTO(entry.getKey(), tarifaActual.getNombreTarifa());
            variacionDTO.setValorInicial(valorInicialRango);
            variacionDTO.setFechaInicial(registroInicial.getFechaModificacion()); // Fecha del primer cambio en el rango
            variacionDTO.setValorFinal(valorFinalActual);
             // Usamos la fecha del último registro ENCONTRADO en el rango como fecha final de referencia del aumento
            variacionDTO.setFechaFinal(registros.get(registros.size() - 1).getFechaModificacion());


            double variacionAbsoluta = valorFinalActual - valorInicialRango;
            variacionDTO.setVariacionAbsoluta(round(variacionAbsoluta, 2));

            if (valorInicialRango != 0) { // Evitar división por cero
                double variacionPorcentual = (variacionAbsoluta / valorInicialRango) * 100;
                variacionDTO.setVariacionPorcentual(round(variacionPorcentual, 2));
            } else {
                variacionDTO.setVariacionPorcentual(variacionAbsoluta > 0 ? Double.POSITIVE_INFINITY : 0.0); // O manejar como prefieras
            }
            reporte.add(variacionDTO);
        }
        // Ordenar reporte final por ID de tarifa o nombre para consistencia
        reporte.sort(Comparator.comparing(VariacionTarifaDTO::getTarifaId));
        return reporte;
    }


    private double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    // *** MÉTODO ACTUALIZADO CON FILTRO DE ESTADO ***
    public ReporteVehiculoCombustibleDTO generarReporteUsoCombustible(
            String vehiculoId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        VehiculoDTO vehiculo = null;
        try {
            vehiculo = viajesClient.getVehiculoById(vehiculoId);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudieron obtener los datos del vehículo " + vehiculoId + ": " + e.getMessage());
        }

        String patente = (vehiculo != null)
            ? String.format("%s - %s %s",
                Optional.ofNullable(vehiculo.getPatente()).orElse("N/A"),
                Optional.ofNullable(vehiculo.getMarca()).orElse("N/A"),
                Optional.ofNullable(vehiculo.getModelo()).orElse("N/A"))
            : "Vehículo no encontrado (ID: " + vehiculoId + ")";


        List<CargaDeCombustible> cargas = cargaDeCombustibleRepository.findAll().stream()
                .filter(c -> c.isEsVigente() && Objects.equals(c.getVehiculoId(), vehiculoId)) // Comparación más segura
                .filter(c -> c.getFecha() != null && !c.getFecha().toLocalDate().isBefore(fechaInicio) && !c.getFecha().toLocalDate().isAfter(fechaFin))
                .collect(Collectors.toList());

        long cantidadCargas = cargas.size();

        double costoTotalCombustible = cargas.stream()
                .filter(c -> c.getPrecioTotal() != null) // Evitar NullPointerException
                .mapToDouble(CargaDeCombustible::getPrecioTotal)
                .sum();

        // --- OBTENER VIAJES Y KILÓMETROS Y FILTRAR POR ESTADO ---
        long cantidadViajesFinalizados = 0L; // Cambiado nombre de variable
        double kilometrosTotalesFinalizados = 0.0; // Cambiado nombre de variable

        try {
            JsonNode viajesResponse = viajesClient.getViajesFiltradosResponse(
                 vehiculoId,
                 fechaInicio.toString(),
                 fechaFin.toString()
            );

            if (viajesResponse != null) {
                // Ya no leemos el 'total' directamente, lo contaremos después de filtrar
                // Sumar kilómetros de la lista "data", filtrando por estado
                if (viajesResponse.hasNonNull("data") && viajesResponse.get("data").isArray()) {
                    ArrayNode dataArray = (ArrayNode) viajesResponse.get("data");
                    for (JsonNode viajeNode : dataArray) {
                        // ---- FILTRO POR ESTADO ----
                        if (viajeNode.hasNonNull("estado") &&
                            ESTADO_VIAJE_FINALIZADO.equalsIgnoreCase(viajeNode.get("estado").asText()))
                        {
                            cantidadViajesFinalizados++; // Incrementar contador solo si está finalizado

                            // Sumar kilómetros solo si está finalizado
                            if (viajeNode.hasNonNull("kilometros") && viajeNode.get("kilometros").isNumber()) {
                                kilometrosTotalesFinalizados += viajeNode.get("kilometros").asDouble(0.0);
                            }
                        }
                        // -------------------------
                    }
                } else {
                     System.err.println("Advertencia: La respuesta de ViajesClient no contiene un array 'data' válido para procesar viajes.");
                }
            } else {
                 System.err.println("Advertencia: La respuesta de ViajesClient fue nula.");
            }
        } catch (Exception e) {
             System.err.println("Error procesando respuesta de ViajesClient para vehículo " + vehiculoId + ": " + e.getMessage());
             // Mantener contadores en 0
        }
        // --- FIN OBTENER Y FILTRAR VIAJES ---


        double viajesPorCarga = cantidadCargas > 0 ? (double) cantidadViajesFinalizados / cantidadCargas : 0.0; // Usar el contador filtrado

        ReporteVehiculoCombustibleDTO dto = new ReporteVehiculoCombustibleDTO(
            patente,
            cantidadViajesFinalizados, // Usar el contador filtrado
            cantidadCargas,
            round(costoTotalCombustible, 2),
            fechaInicio.toString(),
            fechaFin.toString(),
            round(viajesPorCarga, 2),
            round(kilometrosTotalesFinalizados, 2) // Usar la suma filtrada
        );

        return dto;
    }
}