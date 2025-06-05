package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}