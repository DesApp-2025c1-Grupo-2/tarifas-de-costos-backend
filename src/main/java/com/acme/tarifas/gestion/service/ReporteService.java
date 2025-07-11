package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository; // <<-- 1. IMPORTAR
import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.dto.ComparativaTransportistaDTO; // <<-- 2. IMPORTAR
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto; // <<-- 3. IMPORTAR
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections; // <<-- 4. IMPORTAR
import java.util.Comparator; // <<-- 5. IMPORTAR
import java.util.List;
import java.util.stream.Collectors; // <<-- 6. IMPORTAR

@Service
public class ReporteService {

    private final TarifaAdicionalRepository tarifaAdicionalRepository;
    private final TransportistaRepository transportistaRepository;
    private final TarifaCostoRepository tarifaCostoRepository; // <<-- 7. AÑADIR ESTA LÍNEA

    @Autowired
    public ReporteService(TarifaAdicionalRepository tarifaAdicionalRepository,
            TransportistaRepository transportistaRepository, TarifaCostoRepository tarifaCostoRepository) { // <<-- 8.
                                                                                                            // MODIFICAR
                                                                                                            // CONSTRUCTOR
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.transportistaRepository = transportistaRepository;
        this.tarifaCostoRepository = tarifaCostoRepository; // <<-- 9. AÑADIR ESTA LÍNEA
    }

    /**
     * @return
     */
    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }

    public List<TransportistaTarifasDTO> getTransportistasMasUtilizados() {
        return transportistaRepository.findTransportistasMasUtilizados();
    }

    /**
     * @param zonaId
     * @param tipoVehiculoId
     * @param tipoCargaId
     * @return
     */
    public ComparativaTransportistaDTO generarComparativaPorServicio(Long zonaId, Long tipoVehiculoId,
            Long tipoCargaId) {
        List<TarifaCosto> tarifasCoincidentes = tarifaCostoRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoVehiculoId == null
                        || (t.getTipoVehiculo() != null && t.getTipoVehiculo().getId().equals(tipoVehiculoId)))
                .filter(t -> tipoCargaId == null
                        || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
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
}