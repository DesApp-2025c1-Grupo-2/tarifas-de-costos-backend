package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.ProvinciaRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ZonaViajeRepository;
import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.Provincia;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
// Quitar los imports de Logger si ya no se usan
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ZonaViajeService {

    // Quitar el logger si ya no se usa
    // private static final Logger log =
    // LoggerFactory.getLogger(ZonaViajeService.class);

    @Autowired
    private TarifaCostoService tarifaCostoService;

    @Autowired
    private ZonaViajeRepository zonaRepository;

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Transactional(readOnly = true)
    public List<ZonaViajeDTO> getZonasDTO() {
        List<ZonaViaje> zonas = zonaRepository.findAll(); // Asume @EntityGraph en repo
        return zonas.stream()
                .filter(ZonaViaje::getActivo)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ZonaViajeDTO getZonaDTOById(Long id) {
        ZonaViaje zona = zonaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con ID: " + id));

        if (!Hibernate.isInitialized(zona.getProvincias())) {
            Hibernate.initialize(zona.getProvincias());
        }
        return mapToDTO(zona);
    }

    @Transactional(readOnly = true)
    public Optional<ZonaViajeDTO> getZonaDTOByNombre(String nombre) {
        Optional<ZonaViaje> zonaOpt = zonaRepository.findByNombreAndActivoTrue(nombre);
        if (zonaOpt.isPresent()) {
            ZonaViaje zona = zonaOpt.get();
            if (!Hibernate.isInitialized(zona.getProvincias())) {
                Hibernate.initialize(zona.getProvincias());
            }
            return Optional.of(mapToDTO(zona));
        }
        return Optional.empty();
    }

    @Transactional
    public ZonaViajeDTO guardarZonaYDevolverDTO(ZonaViajeDTO zonaDto) {
        if (zonaRepository.existsByNombreAndActivoTrue(zonaDto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una zona activa con ese nombre");
        }

        ZonaViaje zona = new ZonaViaje();
        zona.setNombre(zonaDto.getNombre());
        zona.setDescripcion(zonaDto.getDescripcion());
        zona.setActivo(zonaDto.getActivo() != null ? zonaDto.getActivo() : true);

        Set<Provincia> provincias = mapProvinciasFromNombres(zonaDto.getProvinciasNombres());
        zona.setProvincias(provincias);

        ZonaViaje zonaGuardada = zonaRepository.save(zona);
        return mapToDTO(zonaGuardada);
    }

    @Transactional
    public Optional<ZonaViajeDTO> actualizarZonaYDevolverDTO(Long zonaId, ZonaViajeDTO zonaDto) {
        Optional<ZonaViaje> zonaOpt = zonaRepository.findById(zonaId);
        if (zonaOpt.isEmpty()) {
            return Optional.empty();
        }

        ZonaViaje existente = zonaOpt.get();

        zonaRepository.findByNombreAndActivoTrue(zonaDto.getNombre()).ifPresent(duplicado -> {
            if (!Objects.equals(duplicado.getId(), zonaId)) {
                throw new IllegalArgumentException("Ya existe otra zona activa con ese nombre");
            }
        });

        existente.setNombre(zonaDto.getNombre());
        existente.setDescripcion(zonaDto.getDescripcion());
        existente.setActivo(zonaDto.getActivo() != null ? zonaDto.getActivo() : existente.getActivo());

        Set<Provincia> provincias = mapProvinciasFromNombres(zonaDto.getProvinciasNombres());
        existente.getProvincias().clear();
        existente.getProvincias().addAll(provincias);

        ZonaViaje zonaActualizada = zonaRepository.save(existente);
        return Optional.of(mapToDTO(zonaActualizada));
    }

    @Transactional
    public ZonaViajeDTO bajaYDevolverDTO(Long id) throws Exception {
        ZonaViaje zona = zonaRepository.findById(id)
                .orElseThrow(() -> new Exception("Zona no encontrada con ID: " + id));
        if (!zona.getActivo()) {
            throw new Exception("La zona ya está inactiva");
        }
        zona.setActivo(false);
        ZonaViaje zonaActualizada = zonaRepository.save(zona);
        Hibernate.initialize(zonaActualizada.getProvincias());
        return mapToDTO(zonaActualizada);
    }

    @Transactional(readOnly = true)
    public List<TarifaCosto> obtenerTarifasZona(Long zonaId) {
        return tarifaRepository.findAll().stream()
                .filter(tarifa -> tarifa.getZonaViaje() != null && tarifa.getZonaViaje().getId().equals(zonaId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ZonaViaje> getZonasActivas() {
        return zonaRepository.findAll().stream()
                .filter(ZonaViaje::getActivo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerComparativaCostos(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> resultado = new HashMap<>();
        List<ZonaViaje> zonas = getZonasActivas();
        List<TarifaCosto> todasLasTarifas = tarifaCostoService.getTarifasActivas(fechaInicio, fechaFin);

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

    private ZonaViajeDTO mapToDTO(ZonaViaje zona) {
        ZonaViajeDTO dto = new ZonaViajeDTO();
        dto.setId(zona.getId());
        dto.setNombre(zona.getNombre());
        dto.setDescripcion(zona.getDescripcion());
        dto.setActivo(zona.getActivo());

        if (Hibernate.isInitialized(zona.getProvincias()) && zona.getProvincias() != null) {
            Set<String> nombresProvincias = zona.getProvincias().stream()
                    .map(Provincia::getNombre)
                    .collect(Collectors.toSet());
            dto.setProvinciasNombres(nombresProvincias);
        } else {
            dto.setProvinciasNombres(Collections.emptySet());
            // Considera loggear una advertencia si prefieres, pero quitamos los logs de
            // depuración
            // log.warn("Mapeando DTO para Zona ID {}: ¡Provincias no inicializadas!",
            // zona.getId());
        }
        return dto;
    }

    private Set<Provincia> mapProvinciasFromNombres(Set<String> provinciasNombres) {
        if (provinciasNombres == null || provinciasNombres.isEmpty()) {
            return new HashSet<>();
        }
        return provinciasNombres.stream()
                .map(nombre -> provinciaRepository.findByNombre(nombre)
                        // Lanza la excepción si no se encuentra
                        .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada: " + nombre)))
                .collect(Collectors.toSet());
    }

    @Transactional
    public void eliminarZona(Long id) throws Exception {
        if (!zonaRepository.existsById(id)) {
            throw new Exception("Zona no encontrada para eliminar con ID: " + id);
        }
        zonaRepository.deleteById(id);
    }
}