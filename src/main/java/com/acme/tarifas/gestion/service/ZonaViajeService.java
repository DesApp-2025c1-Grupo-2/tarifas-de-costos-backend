package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ZonaViajeService {

    @Autowired
    private ZonaViajeRepository zonaRepository;

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    public Map<String, Object> obtenerComparativaCostos() {
        Map<String, Object> resultado = new HashMap<>();

        List<ZonaViaje> zonas = zonaRepository.findAll();
        zonas.forEach(zona -> {
            List<TarifaCosto> tarifas = tarifaRepository.findByZonaViaje(zona);
            DoubleSummaryStatistics stats = tarifas.stream()
                    .mapToDouble(TarifaCosto::getValorTotal)
                    .summaryStatistics();

            resultado.put(zona.getNombre(), stats);
        });

        return resultado;
    }

    public List<TarifaCosto> obtenerTarifasZona(Long zonaId) {
        return tarifaRepository.findByZonaViajeId(zonaId);
    }
}