package com.acme.tarifas.gestion.service;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import com.acme.tarifas.gestion.dto.TransportistaTarifasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReporteService {

    private final TarifaAdicionalRepository tarifaAdicionalRepository;
    private final TransportistaRepository transportistaRepository;

    @Autowired
    public ReporteService(TarifaAdicionalRepository tarifaAdicionalRepository, TransportistaRepository transportistaRepository) {
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
        this.transportistaRepository = transportistaRepository;
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
}