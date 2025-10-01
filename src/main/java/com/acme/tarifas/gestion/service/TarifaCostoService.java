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

    @Autowired
    private ViajesClient viajesClient;

    private void procesarYAsociarAdicionales(TarifaCosto tarifa) {
        if (tarifa.getAdicionales() != null) {
            for (TarifaAdicional tarifaAdicional : tarifa.getAdicionales()) {
                Adicional adicionalEnviado = tarifaAdicional.getAdicional();

                if (adicionalEnviado != null && adicionalEnviado.getId() != null) {
                    Adicional adicionalGestionado = adicionalRepository.findById(adicionalEnviado.getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "No se encontró el Adicional con ID: " + adicionalEnviado.getId()));
                    tarifaAdicional.setAdicional(adicionalGestionado);
                } else {
                }
                tarifaAdicional.setTarifaCosto(tarifa);
            }
        }
    }

    @Transactional
    public TarifaCosto crearTarifa(TarifaCosto tarifa) {
        Double valorBase = tarifa.getValorBase();
        if (valorBase == null) {
            throw new IllegalArgumentException("El valor base es obligatorio.");
        }
        if (valorBase <= 0) {
            throw new IllegalArgumentException("El valor base debe ser mayor que cero.");
        }
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());
        procesarYAsociarAdicionales(tarifa);

        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public Optional<TarifaCosto> actualizarTarifa(Long id, TarifaCosto datosNuevos) {
        return tarifaRepository.findById(id).map(tarifaExistente -> {

            crearRegistroHistorial(tarifaExistente, "Actualización general de la tarifa.");

            tarifaExistente.setNombreTarifa(datosNuevos.getNombreTarifa());
            tarifaExistente.setValorBase(datosNuevos.getValorBase());
            tarifaExistente.setTransportista(datosNuevos.getTransportista());
            tarifaExistente.setTipoVehiculo(datosNuevos.getTipoVehiculo());
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

            tarifaExistente.getAdicionales().removeIf(existente -> !nuevosAdicionales.contains(existente));

            for (TarifaAdicional nuevo : nuevosAdicionales) {
                if (tarifaExistente.getAdicionales().contains(nuevo)) {
                    int index = tarifaExistente.getAdicionales().indexOf(nuevo);
                    TarifaAdicional existente = tarifaExistente.getAdicionales().get(index);
                    existente.setCostoEspecifico(nuevo.getCostoEspecifico());
                } else {
                    tarifaExistente.getAdicionales().add(nuevo);
                }
            }

            return tarifaRepository.save(tarifaExistente);
        });
    }

    public List<TarifaCostoDTO> filtrarTarifas(Long tipoVehiculo, Long zona, Long tipoCarga, Long transportista) {
        List<TarifaCosto> todasLasTarifas = tarifaRepository.findAll();
        Stream<TarifaCosto> stream = todasLasTarifas.stream();

        if (tipoVehiculo != null) {
            stream = stream
                    .filter(t -> t.getTipoVehiculoId() != null && t.getTipoVehiculoId().equals(tipoVehiculo));
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
                    .filter(t -> t.getTransportistaId() != null && t.getTransportistaId().equals(transportista));
        }

        return stream.map(tarifaCosto -> {
            TarifaCostoDTO dto = new TarifaCostoDTO(tarifaCosto);

            if (tarifaCosto.getTransportistaId() != null) {
                TransportistaDTO transportistaDTO = viajesClient.getTransportistaById(tarifaCosto.getTransportistaId());
                if (transportistaDTO != null) {
                    dto.setTransportistaNombre(transportistaDTO.getNombreComercial());
                }
            }

            if (tarifaCosto.getTipoVehiculoId() != null) {
                TipoVehiculoDTO tipoVehiculoDTO = viajesClient.getTiposVehiculoById(tarifaCosto.getTipoVehiculoId());
                if (tipoVehiculoDTO != null) {
                    dto.setTipoVehiculoNombre(tipoVehiculoDTO.getNombre());
                }
            }
            return dto;
        }).collect(Collectors.toList());
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

            crearRegistroHistorial(tarifa, "Actualización del valor base.");
            tarifa.setValorBase(nuevoValor);
            tarifa.setFechaUltimaModificacion(LocalDateTime.now());
            tarifa.setVersion(tarifa.getVersion() != null ? tarifa.getVersion() + 1 : 1);
            return tarifaRepository.save(tarifa);
        });
    }

    private void crearRegistroHistorial(TarifaCosto tarifa, String comentario) {
        TarifaCostoHistorial historial = new TarifaCostoHistorial();
        historial.setTarifaOriginal(tarifa);
        historial.setCodigoTarifa(tarifa.getCodigoTarifa());
        historial.setNombreTarifa(tarifa.getNombreTarifa());
        historial.setTipoVehiculo(tarifa.getTipoVehiculo());
        historial.setTipoCargaTarifa(tarifa.getTipoCargaTarifa());
        historial.setZonaViaje(tarifa.getZonaViaje());
        historial.setTransportista(tarifa.getTransportista());
        historial.setValorBase(tarifa.getValorBase());
        historial.setFechaModificacion(LocalDateTime.now());
        historial.setComentarioCambio(comentario);
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

    public List<TarifaCosto> getTarifasActivas() {
        return tarifaRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .collect(Collectors.toList());
    }
}