package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private TarifaAdicionalRepository tarifaAdicionalRepository;

    @Transactional
    public Adicional guardarAdicional(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    public List<Adicional> obtenerTodos() {
        return adicionalRepository.findAll();
    }

    public Optional<Adicional> obtenerPorId(Long id) {
        return adicionalRepository.findById(id);
    }

    @Transactional
    public Optional<Adicional> actualizarAdicional(Long id, Adicional adicionalActualizado) {
        return adicionalRepository.findById(id)
                .map(adicionalExistente -> {
                    if (adicionalActualizado.getNombre() != null) {
                        adicionalExistente.setNombre(adicionalActualizado.getNombre());
                    }
                    if (adicionalActualizado.getCostoDefault() != null) {
                        adicionalExistente.setCostoDefault(adicionalActualizado.getCostoDefault());
                    }
                    if (adicionalActualizado.getDescripcion() != null) {
                        adicionalExistente.setDescripcion(adicionalActualizado.getDescripcion());
                    }
                    return adicionalRepository.save(adicionalExistente);
                });
    }

    @Transactional
    public boolean eliminarAdicional(Long id) {
        return adicionalRepository.findById(id)
                .map(adicional -> {
                    adicionalRepository.delete(adicional);
                    return true;
                })
                .orElse(false);
    }

    public List<Map<String, Object>> obtenerFrecuenciaUsoAdicionales() {
        List<Object[]> resultados = tarifaAdicionalRepository.contarUsoPorAdicional();

        return resultados.stream()
                .map(r -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("idAdicional", r[0]);
                    mapa.put("nombreAdicional", r[1]);
                    mapa.put("vecesUtilizado", r[2]);
                    return mapa;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerAnalisisUsoAdicionales() {
        List<Map<String, Object>> frecuencia = obtenerFrecuenciaUsoAdicionales();

        if (frecuencia.isEmpty()) {
            return Collections.emptyMap();
        }

        // Ordenar por frecuencia de uso
        frecuencia.sort(Comparator.comparingLong(m -> (Long) m.get("vecesUtilizado")));

        Map<String, Object> analisis = new HashMap<>();
        analisis.put("adicionales", frecuencia);
        analisis.put("masUtilizado", frecuencia.get(frecuencia.size() - 1));
        analisis.put("menosUtilizado", frecuencia.get(0));

        return analisis;
    }
}