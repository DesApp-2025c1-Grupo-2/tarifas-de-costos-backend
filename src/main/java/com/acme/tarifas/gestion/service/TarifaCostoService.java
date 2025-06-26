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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TarifaCostoService {

    @Autowired
    private TarifaCostoRepository tarifaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private TarifaAdicionalRepository tarifaAdicionalRepository;

    @Autowired
    private TarifaHistorialRepository historialRepository;

    private void procesarYAsociarAdicionales(TarifaCosto tarifa) {
        if (tarifa.getAdicionales() != null) {
            for (TarifaAdicional tarifaAdicional : tarifa.getAdicionales()) {
                Adicional adicional = tarifaAdicional.getAdicional();

                if (adicional != null && adicional.getId() == null) {

                    Adicional adicionalGuardado = adicionalRepository.save(adicional);

                    tarifaAdicional.setAdicional(adicionalGuardado);
                }

                tarifaAdicional.setTarifaCosto(tarifa);
            }
        }
    }


    @Transactional
    public TarifaCosto crearTarifa(TarifaCosto tarifa) {
        if (tarifa.getValorBase() <= 0) {
            throw new IllegalArgumentException("El valor base debe ser positivo");
        }
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());


        procesarYAsociarAdicionales(tarifa);

        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public Optional<TarifaCosto> actualizarTarifa(Long id, TarifaCosto tarifaActualizada) {
        return tarifaRepository.findById(id).map(tarifaExistente -> {

            tarifaExistente.setNombreTarifa(tarifaActualizada.getNombreTarifa());
            tarifaExistente.setTransportista(tarifaActualizada.getTransportista());
            tarifaExistente.setTipoVehiculo(tarifaActualizada.getTipoVehiculo());
            tarifaExistente.setZonaViaje(tarifaActualizada.getZonaViaje());
            tarifaExistente.setTipoCargaTarifa(tarifaActualizada.getTipoCargaTarifa());
            tarifaExistente.setValorBase(tarifaActualizada.getValorBase());
            tarifaExistente.setFechaUltimaModificacion(LocalDateTime.now());
            tarifaExistente.setVersion(tarifaExistente.getVersion() != null ? tarifaExistente.getVersion() + 1 : 1);

            tarifaExistente.getAdicionales().clear();
            tarifaAdicionalRepository.deleteByTarifaCostoId(id);
            procesarYAsociarAdicionales(tarifaActualizada);
            if (tarifaActualizada.getAdicionales() != null) {
                tarifaExistente.getAdicionales().addAll(tarifaActualizada.getAdicionales());
            }

            return tarifaRepository.save(tarifaExistente);
        });
    }

    public List<TarifaCostoDTO> filtrarTarifas(Long tipoVehiculo, Long zona, Long tipoCarga, Long transportista) {
        List<TarifaCosto> todasLasTarifas = tarifaRepository.findAll();
        Stream<TarifaCosto> stream = todasLasTarifas.stream();

        if (tipoVehiculo != null) {
            stream = stream
                    .filter(t -> t.getTipoVehiculo() != null && t.getTipoVehiculo().getId().equals(tipoVehiculo));
        }
        if (zona != null) {
            stream = stream.filter(t -> t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zona));
        }
        if (tipoCarga != null) {
            stream = stream
                    .filter(t -> t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCarga));
        }
        if (transportista != null) {
            stream = stream
                    .filter(t -> t.getTransportista() != null && t.getTransportista().getId().equals(transportista));
        }

        return stream.map(TarifaCostoDTO::new).collect(Collectors.toList());
    }

    public Optional<TarifaCostoDTO> obtenerTarifaPorId(Long id) {
        return tarifaRepository.findById(id).map(TarifaCostoDTO::new);
    }

    @Transactional
    public Optional<TarifaAdicional> agregarAdicional(Long tarifaId, TarifaAdicional nuevoAdicional) {
        return tarifaRepository.findById(tarifaId).flatMap(tarifa -> {
            Long adicionalId = nuevoAdicional.getAdicional().getId();
            if (adicionalId == null) {
                throw new IllegalArgumentException("Debe especificarse el ID del adicional.");
            }
            return adicionalRepository.findById(adicionalId).map(adicionalExistente -> {
                if (tarifaAdicionalRepository.existsByTarifaCostoIdAndAdicionalId(tarifaId, adicionalId)) {
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
        if (tarifa.getValorBase() == null)
            return 0.0;
        double adicionalesTotal = (tarifa.getAdicionales() == null) ? 0.0
                : tarifa.getAdicionales().stream()
                        .mapToDouble(TarifaAdicional::getCostoEspecifico)
                        .sum();
        return tarifa.getValorBase() + adicionalesTotal;
    }

    public List<TarifaAdicional> obtenerAdicionalesPorTarifa(Long idTarifa) {
        return tarifaAdicionalRepository.findByTarifaCostoId(idTarifa);
    }

    @Transactional
    public void cambiarVigencia(Long id) {
        tarifaRepository.findById(id).ifPresent(tarifa -> {
            tarifa.setEsVigente(false);
            tarifaRepository.save(tarifa);
        });
    }
}