package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.ProvinciaRepository;
import com.acme.tarifas.gestion.entity.Provincia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinciaService {

    @Autowired
    private ProvinciaRepository provinciaRepository;

    public Provincia guardar(Provincia provincia) {
        return provinciaRepository.save(provincia);
    }

    public List<Provincia> obtenerTodas() {
        return provinciaRepository.findAll();
    }

    public Optional<Provincia> obtenerPorId(Long id) {
        return provinciaRepository.findById(id);
    }
}