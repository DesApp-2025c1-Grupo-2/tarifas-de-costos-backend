package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Transactional
    public Adicional guardarAdicional(Adicional adicional) {
        if (adicionalRepository.existsByNombreAndActivoTrue(adicional.getNombre())) {
            throw new IllegalArgumentException("Ya existe un adicional con ese nombre");
        }
        return adicionalRepository.save(adicional);
    }

    public List<Adicional> obtenerTodos() {

        return adicionalRepository.findAll();
    }

    public Optional<Adicional> obtenerPorId(Long id) {
        return adicionalRepository.findById(id);
    }

    @Transactional
    public Optional<Adicional> actualizarCostoDefault(Long id, Double nuevoCosto) {
        return adicionalRepository.findById(id).map(adicional -> {
            adicional.setCostoDefault(nuevoCosto);
            return adicionalRepository.save(adicional);
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

    public Adicional baja(Long id) throws Exception {
        Adicional adicional = adicionalRepository.findById(id)
                .orElseThrow(() -> new Exception("Adicional no encontrado"));

        if (adicional.isActivo()) {
            adicional.setActivo(false);
            return adicionalRepository.save(adicional);
        } else {
            throw new Exception("El adicional ya está inactivo");
        }
    }

    @Transactional
    public Optional<Adicional> actualizarAdicional(Long id, Adicional nuevosDatos) {
        return adicionalRepository.findById(id).map(existente -> {
            adicionalRepository.findByNombreAndActivoTrue(nuevosDatos.getNombre()).ifPresent(duplicado -> {
                if (!Objects.equals(duplicado.getId(), id)) {
                    throw new IllegalArgumentException("Ya existe otro adicional activo con ese nombre");
                }
            });

            existente.setNombre(nuevosDatos.getNombre());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setCostoDefault(nuevosDatos.getCostoDefault());
            existente.setActivo(nuevosDatos.isActivo());
            existente.setEsGlobal(nuevosDatos.isEsGlobal());

            return adicionalRepository.save(existente);
        });
    }
}