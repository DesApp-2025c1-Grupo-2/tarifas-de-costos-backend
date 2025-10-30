package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.CargaDeCombustibleRepository;
import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Importar
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CargaDeCombustibleService {

    @Autowired
    private CargaDeCombustibleRepository cargaRepository;

    // MODIFICADO
    public List<CargaDeCombustible> obtenerTodasLasCargas(LocalDate fechaInicio) {
        if (fechaInicio != null) {
            return cargaRepository.findByEsVigenteTrueAndFechaAfterOrderByFechaDesc(fechaInicio.atStartOfDay());
        }
        return cargaRepository.findByEsVigenteTrueOrderByFechaDesc();
    }

    @Transactional
    public CargaDeCombustible guardarCarga(CargaDeCombustible carga) {
        // ... (el resto del método queda igual)
        // ... (el resto de la clase queda igual)
        // ...
        if (carga.getFecha() == null) {
            carga.setFecha(LocalDateTime.now());
        }
        return cargaRepository.save(carga);
    }

    public Optional<CargaDeCombustible> actualizarCarga(Long id, CargaDeCombustible datosNuevos) {
        return cargaRepository.findById(id).map(existente -> {
            existente.setVehiculoId(datosNuevos.getVehiculoId());
            existente.setFecha(datosNuevos.getFecha());
            existente.setLitrosCargados(datosNuevos.getLitrosCargados());
            existente.setNumeroTicket(datosNuevos.getNumeroTicket());
            existente.setPrecioTotal(datosNuevos.getPrecioTotal());
            return cargaRepository.save(existente);
        });
    }

    @Transactional
    public void darBaja(Long id) {
        cargaRepository.findById(id).ifPresent(carga -> {
            carga.setEsVigente(false);
            cargaRepository.save(carga);
        });
    }
}