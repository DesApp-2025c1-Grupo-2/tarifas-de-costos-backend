package com.acme.tarifas.gestion.service;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.dto.VariacionTarifaDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    public ReporteService(TarifaCostoHistorialRepository historialRepository,
                          TarifaAdicionalRepository tarifaAdicionalRepository,
                          TransportistaRepository transportistaRepository,
                          TarifaCostoRepository tarifaCostoRepository) { 
        this.historialRepository = historialRepository;
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.transportistaRepository = transportistaRepository;
        this.tarifaCostoRepository = tarifaCostoRepository; 
    }

    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        return transportistaRepository.findTransportistasMasUtilizados();
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
}