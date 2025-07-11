package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoCargaTarifaRepository;
import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoCargaTarifaService {

    @Autowired
    private TipoCargaTarifaRepository tipoCargaTarifaRepository;

    public TipoCargaTarifa guardarTipoCargaTarifa(TipoCargaTarifa tipo) {
        if (tipoCargaTarifaRepository.existsByNombreAndActivoTrue(tipo.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de carga activo con ese nombre");
        }
        return tipoCargaTarifaRepository.save(tipo);
    }

    public List<TipoCargaTarifa> obtenerTodosTiposCargaTarifa() {
        return tipoCargaTarifaRepository.findAll();
    }

    public Optional<TipoCargaTarifa> obtenerPorId(Long id) {
        return tipoCargaTarifaRepository.findById(id);
    }

    public void eliminarTipoCargaTarifa(Long id) throws Exception {
        TipoCargaTarifa tipo = tipoCargaTarifaRepository.findById(id)
                .orElseThrow(() -> new Exception("Tipo de carga no encontrado"));
        tipoCargaTarifaRepository.delete(tipo);
    }

    public TipoCargaTarifa baja(Long id) throws Exception {
        TipoCargaTarifa tipoCarga = tipoCargaTarifaRepository.findById(id)
                .orElseThrow(() -> new Exception("tipo de carga no encontrada"));
        if (tipoCarga.isActivo()) {
            tipoCarga.setActivo(false);
            return tipoCargaTarifaRepository.save(tipoCarga);
        } else {
            throw new Exception("El tipo de carga ya está inactivo");
        }
    }

    public Optional<TipoCargaTarifa> actualizarTipo(Long id, TipoCargaTarifa nuevosDatos) {
        return tipoCargaTarifaRepository.findById(id).map(existente -> {
            tipoCargaTarifaRepository.findByNombreAndActivoTrue(nuevosDatos.getNombre()).ifPresent(duplicado -> {
                if (!Objects.equals(duplicado.getId(), id)) {
                    throw new IllegalArgumentException("Ya existe otro tipo de carga activo con ese nombre");
                }
            });

            existente.setNombre(nuevosDatos.getNombre());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setActivo(nuevosDatos.isActivo());

            return tipoCargaTarifaRepository.save(existente);
        });
    }
}