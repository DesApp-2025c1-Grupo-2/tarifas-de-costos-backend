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

import java.util.*;

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
    Map<String, Object> resultado = new LinkedHashMap<>();

    List<ZonaViaje> zonas = zonaRepository.findAll(); // Orden simple

    zonas.forEach(zona -> {
        List<TarifaCosto> tarifas = tarifaRepository.findByZonaViaje(zona);

        if (tarifas.isEmpty()) {
            resultado.put(zona.getNombre(), "No hay tarifas disponibles");
        } else {
            Map<String, Object> estadisticas = new LinkedHashMap<>();

            double suma = tarifas.stream().mapToDouble(TarifaCosto::getValorBase).sum();
            double minimo = tarifas.stream().mapToDouble(TarifaCosto::getValorBase).min().orElse(0);
            double maximo = tarifas.stream().mapToDouble(TarifaCosto::getValorBase).max().orElse(0);
            double promedio = suma / tarifas.size();

            estadisticas.put("cantidad", tarifas.size());
            estadisticas.put("total", suma);
            estadisticas.put("minimo", minimo);
            estadisticas.put("maximo", maximo);
            estadisticas.put("promedio", promedio);

            resultado.put(zona.getNombre(), estadisticas);
        }
    });

    return resultado;
}

    public Map<String, Object> identificarZonasCostosExtremos() {
        List<ZonaViaje> zonas = zonaRepository.findAll();
        Map<String, Object> resultado = new HashMap<>();

        Map<ZonaViaje, Double> promediosPorZona = zonas.stream()
                .collect(Collectors.toMap(
                        zona -> zona,
                        zona -> {
                            List<TarifaCosto> tarifas = tarifaRepository.findByZonaViaje(zona);
                            return tarifas.isEmpty() ? 0.0 :
                                    tarifas.stream().mapToDouble(TarifaCosto::getValorBase).average().orElse(0.0);
                        }
                ));

        // Encontrar zona más cara
        Map.Entry<ZonaViaje, Double> zonaMasCara = promediosPorZona.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Encontrar zona más barata
        Map.Entry<ZonaViaje, Double> zonaMasBarata = promediosPorZona.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        if (zonaMasCara != null) {
            resultado.put("zona_mas_cara", crearResumenZona(zonaMasCara));
        }
        if (zonaMasBarata != null) {
            resultado.put("zona_mas_barata", crearResumenZona(zonaMasBarata));
        }

        return resultado;
    }

    private Map<String, Object> crearResumenZona(Map.Entry<ZonaViaje, Double> entrada) {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("id_zona", entrada.getKey().getId());
        resumen.put("nombre", entrada.getKey().getNombre());
        resumen.put("precio_promedio", entrada.getValue());
        return resumen;
    }

    public List<TarifaCosto> obtenerTarifasZona(Long zonaId) {
        return tarifaRepository.findByZonaViajeId(zonaId);
    }


    public Optional<ZonaViaje> actualizarZona(Long zonaId, ZonaViaje nuevosDatos) {
        return zonaRepository.findById(zonaId).map(existente -> {
            existente.setNombre(nuevosDatos.getNombre());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setRegionMapa(nuevosDatos.getRegionMapa());
            existente.setActivo(nuevosDatos.getActivo());
            return zonaRepository.save(existente);
        });
    }

   

    public ZonaViaje baja(Long id) throws Exception {
        ZonaViaje zona = zonaRepository.findById(id)
                .orElseThrow(() -> new Exception("Zona no encontrada"));

        if (zona.getActivo()) {
            zona.setActivo(false);
            return zonaRepository.save(zona);
        } else {
            throw new Exception("La zona ya está inactiva");
        }
    }
}