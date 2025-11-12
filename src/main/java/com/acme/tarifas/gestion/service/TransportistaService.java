// Archivo: src/main/java/com/acme/tarifas/gestion/service/TransportistaService.java
package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
// Importar TarifaCostoHistorial y su repositorio // Mantenemos por si se usa en otro lado
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dto.HistorialServicioDTO; // Asegúrate que este DTO esté bien
import com.acme.tarifas.gestion.dto.TelefonoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.*;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator; // Para ordenar historial

@Service
public class TransportistaService {

    private final TarifaCostoRepository tarifaCostoRepository;
    private final ViajesClient viajesClient;
    // Añadir repositorio de historial (puede mantenerse por si se usa en otro lado)
    private final TarifaCostoHistorialRepository historialRepository;

    @Autowired
    public TransportistaService(
            TarifaCostoRepository tarifaCostoRepository,
            ViajesClient viajesClient,
            // Inyectar repositorio de historial
            TarifaCostoHistorialRepository historialRepository) {
        this.tarifaCostoRepository = tarifaCostoRepository;
        this.viajesClient = viajesClient;
        this.historialRepository = historialRepository; // Asignar
    }

    // Este método busca tarifas VIGENTES, se mantiene y se usará ahora en el profile
    @Transactional(readOnly = true)
    public List<TarifaCosto> findByTransportistaIdAndEsVigenteTrue(String transportistaId) {
        // Obtenemos todas las tarifas vigentes primero
        List<TarifaCosto> vigentes = tarifaCostoRepository.findByEsVigenteTrue();

        // Filtramos por el ID del transportista específico
        return vigentes.stream()
                .filter(t -> t.getTransportistaId() != null &&
                        t.getTransportistaId().equals(transportistaId))
                // Ordenamos para consistencia, por ejemplo por fecha de creación o nombre
                .sorted(Comparator.comparing(TarifaCosto::getFechaCreacion, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TransportistaProfile> getTransportistaProfile(String id) {
        TransportistaDTO transportista = null;
        try {
             transportista = viajesClient.getTransportistaById(id);
        } catch (Exception e) {
             // Manejar caso donde el transportista no se encuentra en la API de viajes
             System.err.println("Advertencia: No se pudo obtener transportista con ID " + id + " desde ViajesClient: " + e.getMessage());
             return Optional.empty(); // O manejar como prefieras
        }
        // Si llegó aquí pero es null
        if (transportista == null) {
            return Optional.empty();
        }

        // --- INICIO CAMBIO: OBTENER TARIFAS ACTIVAS EN LUGAR DE HISTORIAL ---
        // Buscar las tarifas VIGENTES para este transportista
        List<TarifaCosto> tarifasVigentes = findByTransportistaIdAndEsVigenteTrue(id);

        // Mapear las tarifas vigentes a DTOs para el frontend (reusando HistorialServicioDTO temporalmente)
        List<HistorialServicioDTO> serviciosActivos = tarifasVigentes.stream()
                .map(tarifa -> {
                    HistorialServicioDTO dto = new HistorialServicioDTO();
                    dto.setId(tarifa.getId()); // Usamos el ID de la tarifa
                    // Usamos la fecha de última modificación o creación si la primera es null
                    dto.setFecha(tarifa.getFechaUltimaModificacion() != null ? tarifa.getFechaUltimaModificacion() : tarifa.getFechaCreacion());
                    dto.setNombreTarifaUtilizada(tarifa.getNombreTarifa());
                    dto.setValorTotalTarifa(tarifa.getValorTotal()); // Usamos el valor total calculado
                    dto.setNombreCarga(tarifa.getTipoCargaTarifa() != null ? tarifa.getTipoCargaTarifa().getNombre() : "N/A");
                    return dto;
                })
                .collect(Collectors.toList());
        // --- FIN CAMBIO: OBTENER TARIFAS ACTIVAS ---

        // Obtener tipos de vehículo y zonas (basado en las mismas tarifas vigentes)
        Set<String> tipoVehiculoIds = tarifasVigentes.stream()
                .map(TarifaCosto::getTipoVehiculoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<TipoVehiculoDTO> allTipos = Collections.emptyList(); // Inicializar
         try {
             allTipos = viajesClient.getTiposVehiculo();
         } catch (Exception e) {
             System.err.println("Advertencia: No se pudieron obtener los tipos de vehículo desde ViajesClient: " + e.getMessage());
             // Continuar sin tipos de vehículo si falla
         }

        Set<TipoVehiculoDTO> tiposVehiculo = allTipos.stream()
                .filter(tipo -> tipo.getId() != null && tipoVehiculoIds.contains(tipo.getId()))
                .collect(Collectors.toSet());

        Set<ZonaViaje> zonasOperacion = tarifasVigentes.stream()
                .map(TarifaCosto::getZonaViaje)
                .filter(Objects::nonNull)
                .filter(ZonaViaje::getActivo) // Asegurar que la zona esté activa
                .collect(Collectors.toSet());

        // Pasar los 'serviciosActivos' (tarifas vigentes mapeadas) al constructor del profile
        TransportistaProfile profile = new TransportistaProfile(transportista, tiposVehiculo, zonasOperacion, serviciosActivos);

        return Optional.of(profile);
    }

    @Data
    @NoArgsConstructor
    public static class TransportistaProfile {
        private String id;
        private String nombreEmpresa;
        private String cuit;
        private String contactoNombre;
        private String contactoEmail;
        private TelefonoDTO contactoTelefono;
        private List<TipoVehiculoDTO> tiposVehiculo;
        private List<ZonaViajeDTO> zonasOperacion;
        // El campo se sigue llamando historialServicios, pero ahora contiene las tarifas activas
        private List<HistorialServicioDTO> historialServicios;

        // Constructor actualizado para recibir la lista de servicios activos
        public TransportistaProfile(TransportistaDTO transportista, Set<TipoVehiculoDTO> tiposVehiculo,
                Set<ZonaViaje> zonas, List<HistorialServicioDTO> serviciosActivos) { // Cambiado nombre del parámetro
            this.id = transportista.getId();
            this.nombreEmpresa = transportista.getNombreComercial();
            this.cuit = transportista.getCuit();
            if (transportista.getContacto() != null) {
                this.contactoNombre = transportista.getContacto().getNombre();
                this.contactoEmail = transportista.getContacto().getEmail();
                this.contactoTelefono = transportista.getContacto().getTelefono();
            } else {
                this.contactoNombre = null;
                this.contactoEmail = null;
                this.contactoTelefono = null;
            }

            this.tiposVehiculo = tiposVehiculo.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(TipoVehiculoDTO::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());

            this.zonasOperacion = zonas.stream()
                    .filter(Objects::nonNull)
                    .map(zona -> {
                        // Asegurar inicialización perezosa si es necesario
                        if (!Hibernate.isInitialized(zona.getProvincias())) {
                            Hibernate.initialize(zona.getProvincias());
                        }

                        ZonaViajeDTO dto = new ZonaViajeDTO();
                        dto.setId(zona.getId());
                        dto.setNombre(zona.getNombre());
                        dto.setDescripcion(zona.getDescripcion());
                        dto.setActivo(zona.getActivo());
                        Set<String> nombresProvincias = zona.getProvincias() != null ? zona.getProvincias().stream()
                                .map(Provincia::getNombre)
                                .collect(Collectors.toSet()) : Collections.emptySet();
                        dto.setProvinciasNombres(nombresProvincias);
                        return dto;
                    })
                    .sorted(Comparator.comparing(ZonaViajeDTO::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());

            // Asignar los servicios activos
            this.historialServicios = serviciosActivos != null ? serviciosActivos : Collections.emptyList();
        }
    }
}