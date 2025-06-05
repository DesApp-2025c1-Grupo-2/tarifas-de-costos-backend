package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TarifaHistorialRepository;
import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TarifaCostoService {

    private final TarifaCostoRepository tarifaRepository;
    private final TarifaAdicionalRepository adicionalRepository;
    private final TarifaHistorialRepository historialRepository;

    @Autowired
    public TarifaCostoService(TarifaCostoRepository tarifaRepository,
                              TarifaAdicionalRepository adicionalRepository,
                              TarifaHistorialRepository historialRepository) {
        this.tarifaRepository = tarifaRepository;
        this.adicionalRepository = adicionalRepository;
        this.historialRepository = historialRepository;
    }

    public TarifaCosto crearTarifa(TarifaCosto tarifa) {
        if (tarifa.getValorBase() <= 0) {
            throw new IllegalArgumentException("El valor base debe ser positivo");
        }
        if (tarifaRepository.existsByCodigoTarifa(tarifa.getCodigoTarifa())) {
            throw new IllegalStateException("Ya existe una tarifa con este código");
        }
        return tarifaRepository.save(tarifa);
    }

    @Transactional(readOnly = true)
    public List<TarifaCosto> filtrarTarifas(Long tipoVehiculo, Long zona, Long transportista) {
        return tarifaRepository.findByFilters(tipoVehiculo, zona, transportista);
    }

    @Transactional(readOnly = true)
    public Optional<TarifaCosto> obtenerPorId(Long id) {
        return tarifaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TarifaCosto> obtenerPorIdConAdicionales(Long id) {
        return tarifaRepository.findByIdWithAdicionales(id);
    }

    public TarifaAdicional agregarAdicional(Long tarifaId, Long adicionalId, Double costoEspecifico) {
        TarifaCosto tarifa = tarifaRepository.findById(tarifaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa no encontrada"));

        Adicional adicional = adicionalRepository.findById(adicionalId)
                .orElseThrow(() -> new EntityNotFoundException("Adicional no encontrado")).getAdicional();

        TarifaAdicional tarifaAdicional = new TarifaAdicional();
        tarifaAdicional.setTarifaCosto(tarifa);
        tarifaAdicional.setAdicional(adicional);
        tarifaAdicional.setCostoEspecifico(costoEspecifico);

        return adicionalRepository.save(tarifaAdicional);
    }

    public TarifaCosto actualizarValorBase(Long id, Double nuevoValor) {
        TarifaCosto tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa no encontrada"));

        crearRegistroHistorial(tarifa);
        tarifa.setValorBase(nuevoValor);
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());
        tarifa.setVersion(tarifa.getVersion() + 1);

        return tarifaRepository.save(tarifa);
    }

    private void crearRegistroHistorial(TarifaCosto tarifa) {
        TarifaCostoHistorial historial = new TarifaCostoHistorial();
        historialRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public double calcularTotalTarifa(Long tarifaId) {
        TarifaCosto tarifa = tarifaRepository.findByIdWithAdicionales(tarifaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa no encontrada"));

        return tarifa.getValorTotal();
    }
}