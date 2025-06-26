package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import com.acme.tarifas.gestion.entity.Transportista;
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

    public Adicional baja(Long id) throws Exception{
        Adicional adicional = adicionalRepository.findById(id)
                .orElseThrow(() -> new Exception("Adicional no encontrado"));

        if(adicional.getActivo()){
            adicional.setActivo(false);
            return adicionalRepository.save(adicional);
        }else{
            throw new Exception("El adicional ya estÃ¡ inactivo");
        }
    }

    @Transactional
    public Optional<Adicional> actualizarAdicional(Long id, Adicional nuevosDatos) {
        return adicionalRepository.findById(id).map(existente -> {
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setCostoDefault(nuevosDatos.getCostoDefault());
            existente.setNombre(nuevosDatos.getNombre());
            existente.setActivo(nuevosDatos.getActivo());
            return adicionalRepository.save(existente);
        });
    }
}