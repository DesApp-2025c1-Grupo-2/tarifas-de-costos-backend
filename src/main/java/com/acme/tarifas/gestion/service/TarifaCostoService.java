package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TarifaHistorialRepository;
import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaCostoService {

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private TarifaHistorialRepository historialRepository;

    @Transactional
    public TarifaCosto crearTarifa(TarifaCosto tarifa) {
        if (tarifa.getValorBase() <= 0) {
            throw new IllegalArgumentException("El valor base debe ser positivo");
        }
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());
        return tarifaRepository.save(tarifa);
    }

    public List<TarifaCostoDTO> filtrarTarifas(Long tipoVehiculo, Long zona, Long tipoCarga, Long transportista) {
        return tarifaRepository.findByFilters(tipoVehiculo, zona, tipoCarga, transportista);
        }


    public Optional<TarifaCostoDTO> obtenerTarifaPorId(Long id) {
        return tarifaRepository.findTarifaDTOById(id);
    }

    @Transactional
    public Optional<TarifaAdicional> agregarAdicional(Long tarifaId, TarifaAdicional nuevoAdicional) {
        return tarifaRepository.findById(tarifaId).map(tarifa -> {
            nuevoAdicional.setTarifaCosto(tarifa);
            tarifa.getAdicionales().add(nuevoAdicional);
            tarifaRepository.save(tarifa);
            return nuevoAdicional;
        });
    }

    @Transactional
    public Optional<TarifaCosto> actualizarValorBase(Long id, Double nuevoValor) {
        return tarifaRepository.findById(id).map(tarifa -> {
            crearRegistroHistorial(tarifa);
            tarifa.setValorBase(nuevoValor);
            tarifa.setFechaUltimaModificacion(LocalDateTime.now());
            tarifa.setVersion(tarifa.getVersion() + 1);
            return tarifaRepository.save(tarifa);
        });
    }

    private void crearRegistroHistorial(TarifaCosto tarifa) {
        TarifaCostoHistorial historial = new TarifaCostoHistorial();
        // Copiar todos los campos relevantes
        historial.setTarifaOriginal(tarifa);
        historial.setCodigoTarifa(tarifa.getCodigoTarifa());
        historial.setValorBase(tarifa.getValorBase());
        historial.setFechaModificacion(LocalDateTime.now());
        historialRepository.save(historial);
    }

    public double calcularTotalTarifa(Long tarifaId) {
        return tarifaRepository.findById(tarifaId)
                .map(this::calcularCostoTotal)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
    }

    private double calcularCostoTotal(TarifaCosto tarifa) {
        return tarifa.getValorBase() +
                tarifa.getAdicionales().stream()
                        .mapToDouble(TarifaAdicional::getCostoEspecifico)
                        .sum();
    }
}