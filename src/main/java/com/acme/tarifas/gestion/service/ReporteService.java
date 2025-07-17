/*package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.*;
import com.acme.tarifas.gestion.dto.*;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import com.acme.tarifas.gestion.entity.Viaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final TarifaCostoHistorialRepository historialRepository;
    private final TarifaAdicionalRepository tarifaAdicionalRepository;
    private final TransportistaRepository transportistaRepository;
    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajeRepository viajeRepository;

    @Autowired
    public ReporteService(TarifaCostoHistorialRepository historialRepository,
                          TarifaAdicionalRepository tarifaAdicionalRepository,
                          TransportistaRepository transportistaRepository,
                          TarifaCostoRepository tarifaCostoRepository,
                          ViajeRepository viajeRepository) {
        this.historialRepository = historialRepository;
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.transportistaRepository = transportistaRepository;
        this.tarifaCostoRepository = tarifaCostoRepository;
        this.viajeRepository = viajeRepository;
    }

    // --- INICIO DE LA MODIFICACIÓN ---
    @Transactional(readOnly = true)
    public List<HistorialServicioDTO> generarHistorialServiciosTransportista(Long transportistaId) {
        List<Viaje> viajes = viajeRepository.findByTransportistaId(transportistaId);
        return viajes.stream()
                .map(HistorialServicioDTO::new)
                .collect(Collectors.toList());
    }
    // --- FIN DE LA MODIFICACIÓN ---

    // ... (resto de los métodos existentes)
    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        return transportistaRepository.findTransportistasMasUtilizados();
    }

    public ComparativaTransportistaDTO generarComparativaPorServicio(Long zonaId, Long tipoVehiculoId, Long tipoCargaId) {
        List<TarifaCosto> tarifasCoincidentes = tarifaCostoRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoVehiculoId == null || (t.getTipoVehiculo() != null && t.getTipoVehiculo().getId().equals(tipoVehiculoId)))
                .filter(t -> tipoCargaId == null || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
                .collect(Collectors.toList());

        ComparativaTransportistaDTO comparativaDTO = new ComparativaTransportistaDTO();
        comparativaDTO.setServicio("Comparativa para la selección actual");

        List<ComparativaTransportistaDTO.Comparativa> comparativas = tarifasCoincidentes.stream()
                .filter(t -> t.getTransportista() != null)
                .map(t -> {
                    ComparativaTransportistaDTO.Comparativa c = new ComparativaTransportistaDTO.Comparativa();
                    c.setTransportista(t.getTransportista().getNombreEmpresa());
                    c.setCosto(t.getValorTotal());
                    return c;
                })
                .sorted(Comparator.comparing(ComparativaTransportistaDTO.Comparativa::getCosto))
                .collect(Collectors.toList());

        comparativaDTO.setComparativas(comparativas);
        return comparativaDTO;
    }

    public List<VariacionTarifaDTO> generarComparativaTarifas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<TarifaCostoHistorial> historiales = historialRepository.findAll().stream()
                .filter(h -> !h.getFechaModificacion().toLocalDate().isBefore(fechaInicio) && !h.getFechaModificacion().toLocalDate().isAfter(fechaFin))
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
            if (tarifaActualOpt.isEmpty()) continue;

            TarifaCosto tarifaActual = tarifaActualOpt.get();
            TarifaCostoHistorial registroInicial = registros.get(0);
            TarifaCostoHistorial registroFinal = registros.get(registros.size() - 1);

            VariacionTarifaDTO variacionDTO = new VariacionTarifaDTO(entry.getKey(), tarifaActual.getNombreTarifa());
            variacionDTO.setValorInicial(registroInicial.getValorBase());
            variacionDTO.setFechaInicial(registroInicial.getFechaModificacion());
            variacionDTO.setValorFinal(tarifaActual.getValorBase());
            variacionDTO.setFechaFinal(registroFinal.getFechaModificacion());

            double variacionAbsoluta = tarifaActual.getValorBase() - registroInicial.getValorBase();
            variacionDTO.setVariacionAbsoluta(round(variacionAbsoluta, 2));

            if (registroInicial.getValorBase() != null && registroInicial.getValorBase() != 0) {
                double variacionPorcentual = (variacionAbsoluta / registroInicial.getValorBase()) * 100;
                variacionDTO.setVariacionPorcentual(round(variacionPorcentual, 2));
            } else {
                variacionDTO.setVariacionPorcentual(0.0);
            }
            reporte.add(variacionDTO);
        }
        return reporte;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}*/
package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.*;
import com.acme.tarifas.gestion.dto.*;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import com.acme.tarifas.gestion.entity.Viaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final TarifaCostoHistorialRepository historialRepository;
    private final TarifaAdicionalRepository tarifaAdicionalRepository;
    private final TransportistaRepository transportistaRepository;
    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajeRepository viajeRepository;

    @Autowired
    public ReporteService(TarifaCostoHistorialRepository historialRepository,
                          TarifaAdicionalRepository tarifaAdicionalRepository,
                          TransportistaRepository transportistaRepository,
                          TarifaCostoRepository tarifaCostoRepository,
                          ViajeRepository viajeRepository) {
        this.historialRepository = historialRepository;
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.transportistaRepository = transportistaRepository;
        this.tarifaCostoRepository = tarifaCostoRepository;
        this.viajeRepository = viajeRepository;
    }

    @Transactional(readOnly = true)
    public List<HistorialServicioDTO> generarHistorialServiciosTransportista(Long transportistaId) {
        List<Viaje> viajes = viajeRepository.findByTransportistaId(transportistaId);
        return viajes.stream()
                .map(HistorialServicioDTO::new)
                .collect(Collectors.toList());
    }

    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        return transportistaRepository.findTransportistasMasUtilizados();
    }

    public ComparativaTransportistaDTO generarComparativaPorServicio(Long zonaId, Long tipoVehiculoId, Long tipoCargaId) {
        List<TarifaCosto> tarifasCoincidentes = tarifaCostoRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoVehiculoId == null || (t.getTipoVehiculo() != null && t.getTipoVehiculo().getId().equals(tipoVehiculoId)))
                .filter(t -> tipoCargaId == null || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
                .collect(Collectors.toList());

        ComparativaTransportistaDTO comparativaDTO = new ComparativaTransportistaDTO();
        comparativaDTO.setServicio("Comparativa para la selección actual");

        List<ComparativaTransportistaDTO.Comparativa> comparativas = tarifasCoincidentes.stream()
                .filter(t -> t.getTransportista() != null)
                .map(t -> {
                    ComparativaTransportistaDTO.Comparativa c = new ComparativaTransportistaDTO.Comparativa();
                    c.setTransportista(t.getTransportista().getNombreEmpresa());
                    c.setCosto(t.getValorTotal());
                    return c;
                })
                .sorted(Comparator.comparing(ComparativaTransportistaDTO.Comparativa::getCosto))
                .collect(Collectors.toList());

        comparativaDTO.setComparativas(comparativas);
        return comparativaDTO;
    }

    public List<VariacionTarifaDTO> generarComparativaTarifas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<TarifaCostoHistorial> historiales = historialRepository.findAll().stream()
                .filter(h -> !h.getFechaModificacion().toLocalDate().isBefore(fechaInicio) && !h.getFechaModificacion().toLocalDate().isAfter(fechaFin))
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
            if (tarifaActualOpt.isEmpty() || !tarifaActualOpt.get().isEsVigente()) {
                continue;
            }

            TarifaCosto tarifaActual = tarifaActualOpt.get();
            TarifaCostoHistorial registroInicial = registros.get(0);
            TarifaCostoHistorial registroFinal = registros.get(registros.size() - 1);

            VariacionTarifaDTO variacionDTO = new VariacionTarifaDTO(entry.getKey(), tarifaActual.getNombreTarifa());
            variacionDTO.setValorInicial(registroInicial.getValorBase());
            variacionDTO.setFechaInicial(registroInicial.getFechaModificacion());
            variacionDTO.setValorFinal(tarifaActual.getValorBase());
            variacionDTO.setFechaFinal(registroFinal.getFechaModificacion());

            double variacionAbsoluta = tarifaActual.getValorBase() - registroInicial.getValorBase();
            variacionDTO.setVariacionAbsoluta(round(variacionAbsoluta, 2));

            if (registroInicial.getValorBase() != null && registroInicial.getValorBase() != 0) {
                double variacionPorcentual = (variacionAbsoluta / registroInicial.getValorBase()) * 100;
                variacionDTO.setVariacionPorcentual(round(variacionPorcentual, 2));
            } else {
                variacionDTO.setVariacionPorcentual(0.0);
            }
            reporte.add(variacionDTO);
        }
        return reporte;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}