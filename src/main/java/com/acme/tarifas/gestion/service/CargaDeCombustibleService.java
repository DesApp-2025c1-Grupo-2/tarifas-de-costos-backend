package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.CargaDeCombustibleRepository;
import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CargaDeCombustibleService {

    @Autowired
    private CargaDeCombustibleRepository cargaDeCombustibleRepository;

    public List<CargaDeCombustible> obtenerTodas() {
        return cargaDeCombustibleRepository.findAll();
    }

    public Optional<CargaDeCombustible> obtenerPorId(Long id) {
        return cargaDeCombustibleRepository.findById(id);
    }

    @Transactional
    public CargaDeCombustible guardar(CargaDeCombustible carga) {
        return cargaDeCombustibleRepository.save(carga);
    }

    @Transactional
    public Optional<CargaDeCombustible> actualizar(Long id, CargaDeCombustible nuevosDatos) {
        return cargaDeCombustibleRepository.findById(id).map(existente -> {
            existente.setVehiculoId(nuevosDatos.getVehiculoId());
            existente.setFecha(nuevosDatos.getFecha());
            existente.setCantidadTanques(nuevosDatos.getCantidadTanques());
            existente.setPrecioPorLitro(nuevosDatos.getPrecioPorLitro());
            existente.setCostoTotal(nuevosDatos.getCostoTotal());
            existente.setEsVigente(nuevosDatos.isEsVigente());
            return cargaDeCombustibleRepository.save(existente);
        });
    }

    @Transactional
    public CargaDeCombustible baja(Long id) throws Exception {
        CargaDeCombustible carga = cargaDeCombustibleRepository.findById(id)
                .orElseThrow(() -> new Exception("Carga de combustible no encontrada"));
        if (carga.isEsVigente()) {
            carga.setEsVigente(false);
            return cargaDeCombustibleRepository.save(carga);
        } else {
            throw new Exception("La carga de combustible ya está inactiva");
        }
    }
}