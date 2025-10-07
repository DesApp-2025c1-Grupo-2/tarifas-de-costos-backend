package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.*;
import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.dto.TarifaCostoPayloadDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TarifaCostoService {

    private static final Logger logger = LoggerFactory.getLogger(TarifaCostoService.class);

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
    @Autowired
    private ZonaViajeRepository zonaViajeRepository;
    @Autowired
    private TipoCargaTarifaRepository tipoCargaTarifaRepository;

    @Transactional
    public TarifaCostoDTO crearTarifa(TarifaCostoPayloadDTO payload) {
        if (payload.getValorBase() == null || payload.getValorBase() <= 0) {
            throw new IllegalArgumentException("El valor base es obligatorio y debe ser mayor que cero.");
        }
        TarifaCosto tarifa = new TarifaCosto();
        tarifa.setNombreTarifa(payload.getNombreTarifa());
        tarifa.setValorBase(payload.getValorBase());
        tarifa.setTransportistaId(payload.getTransportistaId());
        tarifa.setTipoVehiculoId(payload.getTipoVehiculoId());
        ZonaViaje zona = zonaViajeRepository.findById(payload.getZonaId())
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con ID: " + payload.getZonaId()));
        TipoCargaTarifa tipoCarga = tipoCargaTarifaRepository.findById(payload.getTipoCargaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de carga no encontrado con ID: " + payload.getTipoCargaId()));
        tarifa.setZonaViaje(zona);
        tarifa.setTipoCargaTarifa(tipoCarga);
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifa.setFechaUltimaModificacion(LocalDateTime.now());
        tarifa.setEsVigente(true);

        TarifaCosto tarifaGuardada = tarifaRepository.save(tarifa);

        if (payload.getAdicionales() != null && !payload.getAdicionales().isEmpty()) {
            List<TarifaAdicional> adicionalesAsociados = procesarYAsociarAdicionales(tarifaGuardada,
                    payload.getAdicionales());
            tarifaGuardada.getAdicionales().addAll(adicionalesAsociados);
        }

        return new TarifaCostoDTO(tarifaRepository.save(tarifaGuardada));
    }

    // --- INICIO DE LA MODIFICACIÓN FINAL ---
    @Transactional
    public Optional<TarifaCostoDTO> actualizarTarifa(Long id, TarifaCostoPayloadDTO payload) {
        return tarifaRepository.findById(id).map(tarifaExistente -> {
            crearRegistroHistorial(tarifaExistente, "Actualización general de la tarifa.");

            tarifaExistente.setNombreTarifa(payload.getNombreTarifa());
            tarifaExistente.setValorBase(payload.getValorBase());
            tarifaExistente.setTransportistaId(payload.getTransportistaId());
            tarifaExistente.setTipoVehiculoId(payload.getTipoVehiculoId());

            ZonaViaje zona = zonaViajeRepository.findById(payload.getZonaId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Zona no encontrada con ID: " + payload.getZonaId()));
            TipoCargaTarifa tipoCarga = tipoCargaTarifaRepository.findById(payload.getTipoCargaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Tipo de carga no encontrado con ID: " + payload.getTipoCargaId()));

            tarifaExistente.setZonaViaje(zona);
            tarifaExistente.setTipoCargaTarifa(tipoCarga);

            tarifaExistente.setFechaUltimaModificacion(LocalDateTime.now());
            tarifaExistente.setVersion(tarifaExistente.getVersion() != null ? tarifaExistente.getVersion() + 1 : 1);

            // Lógica Definitiva y Simplificada para Adicionales:
            // 1. Limpiamos la colección en memoria. Gracias a `orphanRemoval=true` en la
            // entidad TarifaCosto,
            // esto le dice a Hibernate que, al guardar, debe borrar de la base de datos
            // todos los
            // registros de la tabla `Tarifas_Adicionales` que ya no estén en esta lista.
            tarifaExistente.getAdicionales().clear();

            // 2. Procesamos y añadimos las nuevas relaciones de adicionales.
            if (payload.getAdicionales() != null && !payload.getAdicionales().isEmpty()) {
                List<TarifaAdicional> nuevosAdicionales = procesarYAsociarAdicionales(tarifaExistente,
                        payload.getAdicionales());
                tarifaExistente.getAdicionales().addAll(nuevosAdicionales);
            }

            // 3. Guardamos la tarifa. Hibernate ahora sabe que debe borrar los viejos y
            // añadir los nuevos en el orden correcto.
            TarifaCosto tarifaActualizada = tarifaRepository.save(tarifaExistente);
            return new TarifaCostoDTO(tarifaActualizada);
        });
    }
    // --- FIN DE LA MODIFICACIÓN FINAL ---

    private List<TarifaAdicional> procesarYAsociarAdicionales(TarifaCosto tarifa,
            List<TarifaAdicional> adicionalesPayload) {
        return adicionalesPayload.stream()
                .map(taPayload -> {
                    Adicional adicionalOriginal = taPayload.getAdicional();
                    Adicional adicionalGestionado;

                    // Si el ID es nulo o negativo, es un adicional nuevo.
                    if (adicionalOriginal.getId() == null || adicionalOriginal.getId() <= 0) {
                        adicionalOriginal.setId(null); // Aseguramos que sea nulo para que la BD genere un ID nuevo.
                        adicionalGestionado = adicionalRepository.save(adicionalOriginal);
                    } else {
                        // Si el ID es positivo, lo buscamos en la BD.
                        adicionalGestionado = adicionalRepository.findById(adicionalOriginal.getId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Adicional existente no encontrado con ID: " + adicionalOriginal.getId()));
                    }

                    TarifaAdicional nuevaRelacion = new TarifaAdicional();
                    nuevaRelacion.setTarifaCosto(tarifa);
                    nuevaRelacion.setAdicional(adicionalGestionado);
                    nuevaRelacion.setCostoEspecifico(taPayload.getCostoEspecifico());
                    return nuevaRelacion;
                })
                .collect(Collectors.toList());
    }

    public List<TarifaCostoDTO> filtrarTarifas(String tipoVehiculoId, Long zonaId, Long tipoCargaId,
            String transportistaId) {
        Map<String, String> transportistaNombres = getTransportistaNamesMap();
        Map<String, String> tipoVehiculoNombres = getTipoVehiculoNamesMap();
        return tarifaRepository.findAll().stream()
                .filter(TarifaCosto::isEsVigente)
                .filter(t -> tipoVehiculoId == null || tipoVehiculoId.isEmpty()
                        || tipoVehiculoId.equals(t.getTipoVehiculoId()))
                .filter(t -> zonaId == null || (t.getZonaViaje() != null && zonaId.equals(t.getZonaViaje().getId())))
                .filter(t -> tipoCargaId == null
                        || (t.getTipoCargaTarifa() != null && tipoCargaId.equals(t.getTipoCargaTarifa().getId())))
                .filter(t -> transportistaId == null || transportistaId.isEmpty()
                        || transportistaId.equals(t.getTransportistaId()))
                .map(tarifa -> {
                    TarifaCostoDTO dto = new TarifaCostoDTO(tarifa);
                    dto.setTransportistaNombre(transportistaNombres.get(tarifa.getTransportistaId()));
                    dto.setTipoVehiculoNombre(tipoVehiculoNombres.get(tarifa.getTipoVehiculoId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> getTransportistaNamesMap() {
        try {
            List<TransportistaDTO> transportistas = viajesClient.getTransportistas();
            logger.info("Transportistas recibidos de la API externa: {}", transportistas);
            return transportistas.stream()
                    .filter(t -> t != null && t.getId() != null && !t.getNombreParaMostrar().isEmpty())
                    .collect(Collectors.toMap(TransportistaDTO::getId, TransportistaDTO::getNombreParaMostrar,
                            (prev, next) -> prev));
        } catch (Exception e) {
            logger.error("ERROR FATAL al obtener o procesar transportistas de la API externa:", e);
            return Collections.emptyMap();
        }
    }

    private Map<String, String> getTipoVehiculoNamesMap() {
        try {
            List<TipoVehiculoDTO> tiposVehiculo = viajesClient.getTiposVehiculo();
            logger.info("Tipos de Vehículo recibidos de la API externa: {}", tiposVehiculo);
            return tiposVehiculo.stream()
                    .filter(tv -> tv != null && tv.getId() != null && tv.getNombre() != null)
                    .collect(
                            Collectors.toMap(TipoVehiculoDTO::getId, TipoVehiculoDTO::getNombre, (prev, next) -> prev));
        } catch (Exception e) {
            logger.error("ERROR FATAL al obtener o procesar tipos de vehículo de la API externa:", e);
            return Collections.emptyMap();
        }
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

    @Transactional(readOnly = true)
    public Optional<TarifaCostoDTO> obtenerTarifaPorId(Long id) {
        return tarifaRepository.findById(id).map(tarifa -> {
            TarifaCostoDTO dto = new TarifaCostoDTO(tarifa);
            try {
                if (tarifa.getTransportistaId() != null) {
                    TransportistaDTO t = viajesClient.getTransportistaById(tarifa.getTransportistaId());
                    dto.setTransportistaNombre(t.getNombreParaMostrar());
                }
                if (tarifa.getTipoVehiculoId() != null) {
                    TipoVehiculoDTO tv = viajesClient.getTiposVehiculoById(tarifa.getTipoVehiculoId());
                    dto.setTipoVehiculoNombre(tv.getNombre());
                }
            } catch (Exception e) {
                logger.error("Error al enriquecer una tarifa individual con ID: {}", id, e);
            }
            return dto;
        });
    }

    public Optional<TarifaAdicional> agregarAdicional(Long tarifaId, TarifaAdicional nuevoAdicional) {
        return Optional.empty();
    }

    public Optional<TarifaCosto> actualizarValorBase(Long id, Double nuevoValor) {
        return Optional.empty();
    }

    public List<TarifaAdicional> obtenerAdicionalesPorTarifa(Long idTarifa) {
        return Collections.emptyList();
    }

    public void cambiarVigencia(Long id) {
        tarifaRepository.findById(id).ifPresent(tarifa -> {
            tarifa.setEsVigente(false);
            tarifaRepository.save(tarifa);
        });
    }

    public List<TarifaCosto> getTarifasActivas() {
        return tarifaRepository.findByEsVigenteTrue();
    }
}