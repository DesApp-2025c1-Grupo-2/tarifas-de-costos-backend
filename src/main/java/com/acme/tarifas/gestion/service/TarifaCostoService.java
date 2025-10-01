package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.AdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaAdicionalRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TarifaHistorialRepository;
import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private ViajesClient viajesClient;

    @Transactional
    public TarifaCosto crearTarifa(TarifaCosto tarifa) {
        Double valorBase = tarifa.getValorBase();
        if (valorBase == null || valorBase <= 0) {
            throw new IllegalArgumentException("El valor base es obligatorio y debe ser mayor que cero.");
        }
        if (tarifa.getTransportista() != null && tarifa.getTransportista().getId() != null) {
            tarifa.setTransportistaId(tarifa.getTransportista().getId());
        }
        if (tarifa.getTipoVehiculo() != null && tarifa.getTipoVehiculo().getId() != null) {
            tarifa.setTipoVehiculoId(tarifa.getTipoVehiculo().getId());
        }
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());
        procesarYAsociarAdicionales(tarifa);
        return tarifaRepository.save(tarifa);
    }

    private void procesarYAsociarAdicionales(TarifaCosto tarifa) {
        if (tarifa.getAdicionales() != null) {
            for (TarifaAdicional tarifaAdicional : tarifa.getAdicionales()) {
                Adicional adicionalEnviado = tarifaAdicional.getAdicional();
                if (adicionalEnviado != null && adicionalEnviado.getId() != null) {
                    Adicional adicionalGestionado = adicionalRepository.findById(adicionalEnviado.getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "No se encontró el Adicional con ID: " + adicionalEnviado.getId()));
                    tarifaAdicional.setAdicional(adicionalGestionado);
                }
                tarifaAdicional.setTarifaCosto(tarifa);
            }
        }
    }

    @Transactional
    public Optional<TarifaCosto> actualizarTarifa(Long id, TarifaCosto datosNuevos) {
        return tarifaRepository.findById(id).map(tarifaExistente -> {
            crearRegistroHistorial(tarifaExistente, "Actualización general de la tarifa.");
            if (datosNuevos.getTransportista() != null && datosNuevos.getTransportista().getId() != null) {
                tarifaExistente.setTransportistaId(datosNuevos.getTransportista().getId());
            }
            if (datosNuevos.getTipoVehiculo() != null && datosNuevos.getTipoVehiculo().getId() != null) {
                tarifaExistente.setTipoVehiculoId(datosNuevos.getTipoVehiculo().getId());
            }
            tarifaExistente.setNombreTarifa(datosNuevos.getNombreTarifa());
            tarifaExistente.setValorBase(datosNuevos.getValorBase());
            tarifaExistente.setZonaViaje(datosNuevos.getZonaViaje());
            tarifaExistente.setTipoCargaTarifa(datosNuevos.getTipoCargaTarifa());
            tarifaExistente.setFechaUltimaModificacion(LocalDateTime.now());
            tarifaExistente.setVersion(tarifaExistente.getVersion() != null ? tarifaExistente.getVersion() + 1 : 1);
            tarifaExistente.setEsVigente(datosNuevos.isEsVigente());
            List<TarifaAdicional> nuevosAdicionales = new ArrayList<>();
            if (datosNuevos.getAdicionales() != null) {
                for (TarifaAdicional nuevo : datosNuevos.getAdicionales()) {
                    nuevo.setTarifaCosto(tarifaExistente);
                    nuevosAdicionales.add(nuevo);
                }
            }
            tarifaExistente.getAdicionales().clear();
            tarifaExistente.getAdicionales().addAll(nuevosAdicionales);
            return tarifaRepository.save(tarifaExistente);
        });
    }

    public List<TarifaCostoDTO> filtrarTarifas(String tipoVehiculoId, Long zonaId, Long tipoCargaId,
            String transportistaId) {
        Map<String, String> transportistaNombres;
        Map<String, String> tipoVehiculoNombres;
        try {
            transportistaNombres = viajesClient.getTransportistas().stream()
                    .collect(Collectors.toMap(TransportistaDTO::getId, TransportistaDTO::getNombreComercial,
                            (v1, v2) -> v1));

            tipoVehiculoNombres = viajesClient.getTiposVehiculo().stream()
                    .collect(Collectors.toMap(TipoVehiculoDTO::getId, TipoVehiculoDTO::getNombre, (v1, v2) -> v1));
        } catch (Exception e) {
            return Collections.emptyList();
        }

        List<TarifaCosto> tarifasFiltradas = tarifaRepository.findAll().stream()
                .filter(t -> tipoVehiculoId == null || tipoVehiculoId.isEmpty()
                        || (t.getTipoVehiculoId() != null && t.getTipoVehiculoId().equals(tipoVehiculoId)))
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && t.getZonaViaje().getId().equals(zonaId)))
                .filter(t -> tipoCargaId == null
                        || (t.getTipoCargaTarifa() != null && t.getTipoCargaTarifa().getId().equals(tipoCargaId)))
                .filter(t -> transportistaId == null || transportistaId.isEmpty()
                        || (t.getTransportistaId() != null && t.getTransportistaId().equals(transportistaId)))
                .collect(Collectors.toList());

        return tarifasFiltradas.stream().map(tarifaCosto -> {
            TarifaCostoDTO dto = new TarifaCostoDTO(tarifaCosto);
            dto.setTransportistaNombre(transportistaNombres.get(tarifaCosto.getTransportistaId()));
            dto.setTipoVehiculoNombre(tipoVehiculoNombres.get(tarifaCosto.getTipoVehiculoId()));
            return dto;
        }).collect(Collectors.toList());
    }

    private void crearRegistroHistorial(TarifaCosto tarifa, String comentario) {
        TarifaCostoHistorial historial = new TarifaCostoHistorial();
        historial.setTarifaOriginal(tarifa);
        historial.setCodigoTarifa(tarifa.getCodigoTarifa());
        historial.setNombreTarifa(tarifa.getNombreTarifa());
        historial.setTipoVehiculoId(tarifa.getTipoVehiculoId());
        historial.setTransportistaId(tarifa.getTransportistaId());
        historial.setTipoCargaTarifa(tarifa.getTipoCargaTarifa());
        historial.setZonaViaje(tarifa.getZonaViaje());
        historial.setValorBase(tarifa.getValorBase());
        historial.setFechaModificacion(LocalDateTime.now());
        historial.setComentarioCambio(comentario);
        historialRepository.save(historial);
    }

    public Optional<TarifaCostoDTO> obtenerTarifaPorId(Long id) {
        return tarifaRepository.findById(id).map(TarifaCostoDTO::new);
    }

    @Transactional
    public Optional<TarifaAdicional> agregarAdicional(Long tarifaId, TarifaAdicional nuevoAdicional) {
        return Optional.empty();
    }

    @Transactional
    public Optional<TarifaCosto> actualizarValorBase(Long id, Double nuevoValor) {
        return Optional.empty();
    }

    public double calcularTotalTarifa(Long tarifaId) {
        return 0.0;
    }

    public List<TarifaAdicional> obtenerAdicionalesPorTarifa(Long idTarifa) {
        return Collections.emptyList();
    }

    @Transactional
    public void cambiarVigencia(Long id) {
    }

    public List<TarifaCosto> getTarifasActivas() {
        return Collections.emptyList();
    }
}