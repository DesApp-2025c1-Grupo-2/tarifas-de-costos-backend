package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ZonaViajeService {

    @Autowired
    private ZonaViajeRepository zonaRepository;

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    public List<ZonaViaje> getZonas() {
        return zonaRepository.findAll();
    }

    public Optional<ZonaViaje> getZonaById(Long id) {
        return zonaRepository.findById(id);
    }

    @Transactional
    public void eliminarZona(Long id) throws Exception {
        ZonaViaje zonaViaje = zonaRepository.findById(id)
                .orElseThrow(() -> new Exception("Zona no encontrada"));
        zonaRepository.deleteById(id);
    }

    public ZonaViaje guardarZona(ZonaViaje zona) {
        return zonaRepository.save(zona);
    }

    public Map<String, Object> obtenerComparativaCostos() {
        Map<String, Object> resultado = new HashMap<>();
        List<ZonaViaje> zonas = zonaRepository.findAll();
        List<TarifaCosto> todasLasTarifas = tarifaRepository.findAll();

        zonas.forEach(zona -> {
            List<TarifaCosto> tarifasDeLaZona = todasLasTarifas.stream()
                    .filter(tarifa -> tarifa.getZonaViaje() != null
                            && tarifa.getZonaViaje().getId().equals(zona.getId()))
                    .collect(Collectors.toList());
            if (tarifasDeLaZona.isEmpty()) {
                resultado.put(zona.getNombre(), "No hay tarifas");
            } else {
                DoubleSummaryStatistics stats = tarifasDeLaZona.stream()
                        .mapToDouble(TarifaCosto::getValorTotal)
                        .summaryStatistics();
                resultado.put(zona.getNombre(), stats);
            }
        });

        return resultado;
    }

    @Transactional
    public Optional<ZonaViaje> actualizarZona(Long zonaId, ZonaViaje nuevosDatos) {
        return zonaRepository.findById(zonaId).map(existente -> {
            existente.setNombre(nuevosDatos.getNombre());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setRegionMapa(nuevosDatos.getRegionMapa());
            existente.setActivo(nuevosDatos.isActivo());
            return zonaRepository.save(existente);
        });
    }

    public List<TarifaCosto> obtenerTarifasZona(Long zonaId) {
        return tarifaRepository.findAll().stream()
                .filter(tarifa -> tarifa.getZonaViaje() != null && tarifa.getZonaViaje().getId().equals(zonaId))
                .collect(Collectors.toList());
    }

    public ZonaViaje baja(Long id) throws Exception {
        ZonaViaje zona = zonaRepository.findById(id)
                .orElseThrow(() -> new Exception("Zona no encontrada"));
        if (zona.isActivo()) {
            zona.setActivo(false);
            return zonaRepository.save(zona);
        } else {
            throw new Exception("La zona ya está inactiva");
        }
    }
}