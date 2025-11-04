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
import com.acme.tarifas.gestion.entity.ZonaViaje;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
            fin = fechaFin.atTime(23, 59, 59);
        }

        if (inicio == null && fin == null) {
            return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
        }

        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionalesByFechaCreacion(inicio, fin);
    }

    public ComparativaTransportistaDTO generarComparativaPorServicio(Long zonaId, String tipoVehiculoId,
            Long tipoCargaId) {
        List<TarifaCosto> tarifasCoincidentes = tarifaCostoRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoVehiculoId == null || (t.getTipoVehiculoId() != null
                        && t.getTipoVehiculoId().equals(tipoVehiculoId)))
                .filter(t -> tipoCargaId == null
                        || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
                .collect(Collectors.toList());

        Map<String, TransportistaDTO> transportistasMap = viajesClient.getTransportistas().stream()
                .collect(Collectors.toMap(TransportistaDTO::getId, Function.identity()));

        ComparativaTransportistaDTO comparativaDTO = new ComparativaTransportistaDTO();
        comparativaDTO.setServicio("Comparativa para la selección actual");

        List<ComparativaTransportistaDTO.Comparativa> comparativas = tarifasCoincidentes.stream()
                .filter(t -> t.getTransportistaId() != null && transportistasMap.containsKey(t.getTransportistaId()))
                .map(t -> {
                    TransportistaDTO transportista = transportistasMap.get(t.getTransportistaId());
                    ComparativaTransportistaDTO.Comparativa c = new ComparativaTransportistaDTO.Comparativa();
                    c.setTransportista(transportista.getNombreComercial() != null ? transportista.getNombreComercial()
                            : "Nombre no disponible");
                    c.setCosto(t.getValorTotal());
                    c.setTarifaId(t.getId());
                    c.setNombreTarifa(t.getNombreTarifa() != null ? t.getNombreTarifa() : "Tarifa sin nombre");
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
                .filter(h -> h.getFechaModificacion() != null && !h.getFechaModificacion().isBefore(inicio)
                        && !h.getFechaModificacion().isAfter(fin))
                .sorted(Comparator.comparing(TarifaCostoHistorial::getFechaModificacion))
                .collect(Collectors.toList());

        Map<Long, List<TarifaCostoHistorial>> historialesPorTarifa = historiales.stream()
                .filter(h -> h.getTarifaOriginal() != null)
                .collect(Collectors.groupingBy(h -> h.getTarifaOriginal().getId()));

        List<VariacionTarifaDTO> reporte = new ArrayList<>();

        for (Map.Entry<Long, List<TarifaCostoHistorial>> entry : historialesPorTarifa.entrySet()) {
            List<TarifaCostoHistorial> registros = entry.getValue();
            if (registros.isEmpty())
                continue;

            Optional<TarifaCosto> tarifaActualOpt = tarifaCostoRepository.findById(entry.getKey());
            if (tarifaActualOpt.isEmpty()) {
                continue;
            }

            TarifaCosto tarifaActual = tarifaActualOpt.get();
            TarifaCostoHistorial registroInicial = registros.get(0);
            Double valorFinalActual = tarifaActual.getValorBase();
            Double valorInicialRango = registroInicial.getValorBase();

            if (valorInicialRango == null || valorFinalActual == null) {
                continue;
            }

            VariacionTarifaDTO variacionDTO = new VariacionTarifaDTO(entry.getKey(), tarifaActual.getNombreTarifa());
            variacionDTO.setValorInicial(valorInicialRango);
            variacionDTO.setFechaInicial(registroInicial.getFechaModificacion());
            variacionDTO.setValorFinal(valorFinalActual);

            variacionDTO.setFechaFinal(registros.get(registros.size() - 1).getFechaModificacion());

            double variacionAbsoluta = valorFinalActual - valorInicialRango;
            variacionDTO.setVariacionAbsoluta(round(variacionAbsoluta, 2));

            if (valorInicialRango != 0) {
                double variacionPorcentual = (variacionAbsoluta / valorInicialRango) * 100;
                variacionDTO.setVariacionPorcentual(round(variacionPorcentual, 2));
            } else {
                variacionDTO.setVariacionPorcentual(variacionAbsoluta > 0 ? Double.POSITIVE_INFINITY : 0.0);
            }
            reporte.add(variacionDTO);
        }

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

    public ReporteVehiculoCombustibleDTO generarReporteUsoCombustible(
            String vehiculoId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        VehiculoDTO vehiculo = null;
        try {
            vehiculo = viajesClient.getVehiculoById(vehiculoId);
        } catch (Exception e) {
            System.err.println(
                    "Advertencia: No se pudieron obtener los datos del vehículo " + vehiculoId + ": " + e.getMessage());
        }

        String patente = (vehiculo != null)
                ? String.format("%s - %s %s",
                        Optional.ofNullable(vehiculo.getPatente()).orElse("N/A"),
                        Optional.ofNullable(vehiculo.getMarca()).orElse("N/A"),
                        Optional.ofNullable(vehiculo.getModelo()).orElse("N/A"))
                : "Vehículo no encontrado (ID: " + vehiculoId + ")";

        List<CargaDeCombustible> cargas = cargaDeCombustibleRepository.findAll().stream()
                .filter(c -> c.isEsVigente() && Objects.equals(c.getVehiculoId(), vehiculoId))
                .filter(c -> c.getFecha() != null && !c.getFecha().toLocalDate().isBefore(fechaInicio)
                        && !c.getFecha().toLocalDate().isAfter(fechaFin))
                .collect(Collectors.toList());

        long cantidadCargas = cargas.size();

        double costoTotalCombustible = cargas.stream()
                .filter(c -> c.getPrecioTotal() != null)
                .mapToDouble(CargaDeCombustible::getPrecioTotal)
                .sum();

        double litrosTotales = cargas.stream()
                .filter(c -> c.getLitrosCargados() != null)
                .mapToDouble(CargaDeCombustible::getLitrosCargados)
                .sum();

        List<ReporteVehiculoCombustibleDTO.CargaEventoDTO> cargasEventos = cargas.stream()
                .map(c -> new ReporteVehiculoCombustibleDTO.CargaEventoDTO(
                        c.getFecha().toLocalDate(),
                        c.getLitrosCargados()))
                .collect(Collectors.toList());

        long cantidadViajesFinalizados = 0L;
        double kilometrosTotalesFinalizados = 0.0;
        List<ReporteVehiculoCombustibleDTO.ViajeEventoDTO> viajesEventos = new ArrayList<>();

        try {
            JsonNode viajesResponse = viajesClient.getViajesFiltradosResponse(
                    vehiculoId,
                    fechaInicio.toString(),
                    fechaFin.toString());

            if (viajesResponse != null && viajesResponse.hasNonNull("data") && viajesResponse.get("data").isArray()) {
                ArrayNode dataArray = (ArrayNode) viajesResponse.get("data");

                for (JsonNode viajeNode : dataArray) {

                    String estado = viajeNode.hasNonNull("estado") ? viajeNode.get("estado").asText("N/A") : "N/A";
                    double kmViaje = 0.0;

                    if (viajeNode.hasNonNull("kilometros") && viajeNode.get("kilometros").isNumber()) {
                        kmViaje = viajeNode.get("kilometros").asDouble(0.0);
                    }

                    if (ESTADO_VIAJE_FINALIZADO.equalsIgnoreCase(estado)) {

                        cantidadViajesFinalizados++;
                        kilometrosTotalesFinalizados += kmViaje;

                        // --- CORRECCIÓN AQUÍ ---
                        // Usar fecha_inicio ya que fecha_fin no está disponible
                        if (viajeNode.hasNonNull("fecha_inicio") && kmViaje > 0) {
                            try {
                                // Parsea la fecha de inicio
                                LocalDate fechaInicioViaje = LocalDateTime
                                        .parse(viajeNode.get("fecha_inicio").asText().replace("Z", "")).toLocalDate();

                                // Comprueba que la fecha de inicio esté DENTRO del rango del filtro
                                if (!fechaInicioViaje.isBefore(fechaInicio) && !fechaInicioViaje.isAfter(fechaFin)) {
                                    viajesEventos.add(new ReporteVehiculoCombustibleDTO.ViajeEventoDTO(
                                            fechaInicioViaje, // Usar la fecha de inicio
                                            kmViaje,
                                            estado));
                                }
                            } catch (DateTimeParseException e) {
                                System.err.println("Error al parsear fecha_inicio de viaje: "
                                        + viajeNode.get("fecha_inicio").asText());
                            }
                        }
                    }
                }
            } else {
                System.err.println(
                        "Advertencia: La respuesta de ViajesClient no contiene un array 'data' válido para procesar viajes.");
            }
        } catch (Exception e) {
            System.err.println(
                    "Error procesando respuesta de ViajesClient para vehículo " + vehiculoId + ": " + e.getMessage());

        }

        double viajesPorCarga = cantidadCargas > 0 ? (double) cantidadViajesFinalizados / cantidadCargas : 0.0;

        return new ReporteVehiculoCombustibleDTO(
                patente,
                cantidadViajesFinalizados,
                cantidadCargas,
                round(costoTotalCombustible, 2),
                fechaInicio.toString(),
                fechaFin.toString(),
                round(viajesPorCarga, 2),
                round(kilometrosTotalesFinalizados, 2),
                round(litrosTotales, 2),
                viajesEventos,
                cargasEventos);
    }
}