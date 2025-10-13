/*package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.CargaDeCombustibleRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dto.ComparativaTransportistaDTO;
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.ReporteVehiculoCombustibleDTO;
import org.springframework.transaction.annotation.Transactional;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.dto.VariacionTarifaDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajesClient viajesClient;
    private final CargaDeCombustibleRepository cargaDeCombustibleRepository;

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
    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
    return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }   

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        List<Object[]> rawResults = tarifaCostoRepository.countByTransportista();

        return rawResults.stream()
                .map(obj -> {
                    String transportistaId = (String) obj[0];
                    Long cantidad = (Long) obj[1];


                    TransportistaDTO dto = viajesClient.getTransportistaById(transportistaId);


                    return new TransportistaTarifasDTO(
                            dto.getNombreComercial(),
                            cantidad
                    );
                })
                .toList();
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
                    c.setTransportista(t.getTransportista().getNombreComercial());
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
    
    // [NUEVO MÉTODO DE REPORTE]
    public ReporteVehiculoCombustibleDTO generarReporteUsoCombustible(String vehiculoId, LocalDate fechaInicio, LocalDate fechaFin) {
        
        // 1. Obtener datos de Viajes (API Externa)
        Long cantidadViajes = viajesClient.getCantidadViajesVehiculo(
            vehiculoId, 
            fechaInicio.toString(), 
            fechaFin.toString()
        );

        // 2. Obtener datos de Combustible (DB Local)
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atStartOfDay().withHour(23).withMinute(59).withSecond(59);

        List<CargaDeCombustible> cargas = cargaDeCombustibleRepository
            .findByVehiculoIdAndFechaBetweenAndEsVigenteTrue(vehiculoId, inicio, fin);

        Long cantidadCargas = (long) cargas.size();
        Double costoTotal = cargas.stream()
            .mapToDouble(CargaDeCombustible::getCostoTotal)
            .sum();

        // 3. Obtener nombre del vehículo para el reporte (API Externa)
        VehiculoDTO vehiculoDTO = viajesClient.getVehiculoById(vehiculoId);
        String patente = (vehiculoDTO != null) ? vehiculoDTO.getPatente() : "Vehículo No Encontrado";

        // 4. Calcular métricas de eficiencia
        Double viajesPorCarga = (cantidadCargas > 0) 
            ? (double) cantidadViajes / cantidadCargas 
            : 0.0;

        // 5. Construir y devolver el DTO
        return new ReporteVehiculoCombustibleDTO(
            patente,
            cantidadViajes,
            cantidadCargas,
            round(costoTotal, 2),
            fechaInicio.toString(),
            fechaFin.toString(),
            round(viajesPorCarga, 2)
        );
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}*/

package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.CargaDeCombustibleRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dto.ComparativaTransportistaDTO;
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.ReporteVehiculoCombustibleDTO;
import org.springframework.transaction.annotation.Transactional;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.dto.VariacionTarifaDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajesClient viajesClient;
    private final CargaDeCombustibleRepository cargaDeCombustibleRepository;

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

        return rawResults.stream()
                .map(obj -> {
                    String transportistaId = (String) obj[0];
                    Long cantidad = (Long) obj[1];


                    TransportistaDTO dto = viajesClient.getTransportistaById(transportistaId);


                    return new TransportistaTarifasDTO(
                            dto.getNombreComercial(),
                            cantidad
                    );
                })
                .toList();
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
                    c.setTransportista(t.getTransportista().getNombreComercial());
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
    
    // [NUEVO MÉTODO DE REPORTE]
    public ReporteVehiculoCombustibleDTO generarReporteUsoCombustible(String vehiculoId, LocalDate fechaInicio, LocalDate fechaFin) {
        
        // 1. Obtener datos de Viajes (API Externa)
        Long cantidadViajes = viajesClient.getCantidadViajesVehiculo(
            vehiculoId, 
            fechaInicio.toString(), 
            fechaFin.toString()
        );

        // 2. Obtener datos de Combustible (DB Local)
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atStartOfDay().withHour(23).withMinute(59).withSecond(59);

        List<CargaDeCombustible> cargas = cargaDeCombustibleRepository
            .findByVehiculoIdAndFechaBetweenAndEsVigenteTrue(vehiculoId, inicio, fin);

        Long cantidadCargas = (long) cargas.size();
        Double costoTotal = cargas.stream()
            .mapToDouble(CargaDeCombustible::getCostoTotal)
            .sum();

        // 3. Obtener nombre del vehículo para el reporte (API Externa)
        VehiculoDTO vehiculoDTO = viajesClient.getVehiculoById(vehiculoId);
        String patente = (vehiculoDTO != null) ? vehiculoDTO.getPatente() : "Vehículo No Encontrado";

        // 4. Calcular métricas de eficiencia
        Double viajesPorCarga = (cantidadCargas > 0) 
            ? (double) cantidadViajes / cantidadCargas 
            : 0.0;

        // 5. Construir y devolver el DTO
        return new ReporteVehiculoCombustibleDTO(
            patente,
            cantidadViajes,
            cantidadCargas,
            round(costoTotal, 2),
            fechaInicio.toString(),
            fechaFin.toString(),
            round(viajesPorCarga, 2)
        );
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}