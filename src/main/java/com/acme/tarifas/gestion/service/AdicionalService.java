package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Transactional
    public Adicional guardarAdicional(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    // --- INICIO DE LA MODIFICACIÓN ---
    // Ahora, por defecto, solo se obtienen los adicionales globales.
    public List<Adicional> obtenerTodos() {
        return adicionalRepository.findAllGlobales();
    }
    // --- FIN DE LA MODIFICACIÓN ---

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
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setCostoDefault(nuevosDatos.getCostoDefault());
            existente.setNombre(nuevosDatos.getNombre());
            existente.setActivo(nuevosDatos.isActivo());
            return adicionalRepository.save(existente);
        });
    }
}