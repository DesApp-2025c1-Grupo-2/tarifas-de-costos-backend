package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.ProvinciaRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
import com.acme.tarifas.gestion.entity.Provincia;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ZonaViajeService {

    @Autowired
    private TarifaCostoService tarifaCostoService;

    @Autowired
    private ZonaViajeRepository zonaRepository;

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    public List<ZonaViaje> getZonas() {
        return zonaRepository.findAll();
    }

    public Optional<ZonaViaje> getZonaById(Long id) {
        return zonaRepository.findById(id);
    }

    public Optional<ZonaViaje> getZonaByNombre(String nombre) {
        return zonaRepository.findByNombreAndActivoTrue(nombre);
    }

    @Transactional
    public void eliminarZona(Long id) throws Exception {
        ZonaViaje zonaViaje = zonaRepository.findById(id)
                .orElseThrow(() -> new Exception("Zona no encontrada"));
        zonaRepository.deleteById(id);
    }

    public ZonaViaje guardarZona(ZonaViaje zona, Set<Long> provinciaIds) {
        if (zonaRepository.existsByNombreAndActivoTrue(zona.getNombre())) {
            throw new IllegalArgumentException("Ya existe una zona activa con ese nombre");
        }
        Set<Provincia> provincias = new HashSet<>(provinciaRepository.findAllById(provinciaIds));
        zona.setProvincias(provincias);
        return zonaRepository.save(zona);
    }

    public Map<String, Object> obtenerComparativaCostos() {
        Map<String, Object> resultado = new HashMap<>();
        List<ZonaViaje> zonas = getZonasActivas();

        List<TarifaCosto> todasLasTarifas = tarifaCostoService.getTarifasActivas();

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
    public Optional<ZonaViaje> actualizarZona(Long zonaId, ZonaViaje nuevosDatos, Set<Long> provinciaIds) {
        return zonaRepository.findById(zonaId).map(existente -> {
            zonaRepository.findByNombreAndActivoTrue(nuevosDatos.getNombre()).ifPresent(duplicado -> {
                if (!Objects.equals(duplicado.getId(), zonaId)) {
                    throw new IllegalArgumentException("Ya existe otra zona activa con ese nombre");
                }
            });

            existente.setNombre(nuevosDatos.getNombre());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setRegionMapa(nuevosDatos.getRegionMapa());
            existente.setActivo(nuevosDatos.isActivo());
            Set<Provincia> provincias = new HashSet<>(provinciaRepository.findAllById(provinciaIds));
            existente.setProvincias(provincias);
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

    public List<ZonaViaje> getZonasActivas() {
        return zonaRepository.findAll().stream()
                .filter(ZonaViaje::isActivo)
                .collect(Collectors.toList());
    }
}