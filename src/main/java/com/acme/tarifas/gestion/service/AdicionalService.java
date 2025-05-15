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

    public List<Adicional> obtenerTodos() {
        return adicionalRepository.findAll();
    }

    @Transactional
    public Optional<Adicional> actualizarCostoDefault(Long id, Double nuevoCosto) {
        return adicionalRepository.findById(id).map(adicional -> {
            adicional.setCostoDefault(nuevoCosto);
            return adicionalRepository.save(adicional);
        });
    }
}