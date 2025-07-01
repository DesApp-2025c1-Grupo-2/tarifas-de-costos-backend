package com.acme.tarifas.gestion.service;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dto.FrecuenciaAdicionalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReporteService {

    private final TarifaAdicionalRepository tarifaAdicionalRepository;

    @Autowired
    public ReporteService(TarifaAdicionalRepository tarifaAdicionalRepository) {
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
    }

    /**
     * @return 
     */
    public List<FrecuenciaAdicionalDTO> getFrecuenciaUsoAdicionales() {
        return tarifaAdicionalRepository.findFrecuenciaUsoAdicionales();
    }
}