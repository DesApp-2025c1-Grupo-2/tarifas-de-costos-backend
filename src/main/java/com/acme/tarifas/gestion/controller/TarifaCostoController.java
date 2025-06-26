package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TarifaHistorialRepository;
import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
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
    private final TarifaAdicionalRepository tarifaAdicionalRepository;

    @Autowired
    public TarifaCostoService(TarifaCostoRepository tarifaRepository,
                              TarifaAdicionalRepository adicionalRepository,
                              TarifaHistorialRepository historialRepository,
                              TarifaAdicionalRepository tarifaAdicionalRepository) {
        this.tarifaRepository = tarifaRepository;
        this.adicionalRepository = adicionalRepository;
        this.historialRepository = historialRepository;
        this.tarifaAdicionalRepository = tarifaAdicionalRepository;
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

    public List<TarifaCostoDTO> filtrarTarifas(Long tipoVehiculo, Long zona, Long tipoCarga, Long transportista) {
        return tarifaRepository.findByFilters(tipoVehiculo, zona, tipoCarga, transportista);
    }

    public Optional<TarifaCostoDTO> obtenerTarifaPorId(Long id) {
        return tarifaRepository.findTarifaDTOById(id);
    }

    @Transactional
    public Optional<TarifaAdicional> agregarAdicional(Long tarifaId, TarifaAdicional nuevoAdicional) {
        return tarifaRepository.findById(tarifaId).flatMap(tarifa -> {
            Long adicionalId = nuevoAdicional.getAdicional().getId();

            if (adicionalId == null) {
                throw new IllegalArgumentException("Debe especificarse el ID del adicional.");
            }

            return adicionalRepository.findById(adicionalId).map(adicionalExistente -> {
                boolean yaExiste = tarifaAdicionalRepository.existsByTarifaCostoIdAndAdicionalId(tarifaId, adicionalId);
                if (yaExiste) {
                    throw new IllegalArgumentException("Ya existe este adicional en la tarifa.");
                }

                if (nuevoAdicional.getCostoEspecifico() == null) {
                    nuevoAdicional.setCostoEspecifico(adicionalExistente.getCostoDefault());
                }

                nuevoAdicional.setTarifaCosto(tarifa);
                nuevoAdicional.setAdicional(adicionalExistente);

                tarifa.getAdicionales().add(nuevoAdicional);
                tarifaRepository.save(tarifa);

                return nuevoAdicional;
            });
        });
    }

    @Transactional(readOnly = true)
    public Optional<TarifaCosto> obtenerPorId(Long id) {
        return tarifaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TarifaCosto> obtenerPorIdConAdicionales(Long id) {
        return tarifaRepository.findByIdWithAdicionales(id);
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
        // Acá deberías completar con los datos reales que querés guardar en el historial
        historialRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public double calcularTotalTarifa(Long tarifaId) {
        TarifaCosto tarifa = tarifaRepository.findByIdWithAdicionales(tarifaId)
                .orElseThrow(() -> new EntityNotFoundException("Tarifa no encontrada"));

        return tarifa.getValorTotal();
    }

    public List<TarifaAdicional> obtenerAdicionalesPorTarifa(Long idTarifa){
        return tarifaAdicionalRepository.findByTarifaCostoId(idTarifa);
    }

    @Transactional
    public void cambiarVigencia(Long id) {
        tarifaRepository.findById(id).ifPresent(t -> t.setEsVigente(!t.getEsVigente()));
    }
}