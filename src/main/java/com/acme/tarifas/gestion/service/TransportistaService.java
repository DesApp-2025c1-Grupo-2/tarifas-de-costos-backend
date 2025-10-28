// Archivo: src/main/java/com/acme/tarifas/gestion/service/TransportistaService.java
package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
// Importar TarifaCostoHistorial y su repositorio
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
    // Añadir repositorio de historial
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

    // Este método busca tarifas VIGENTES, se mantiene si es necesario en otro lugar
    @Transactional(readOnly = true)
    public List<TarifaCosto> findByTransportistaIdAndEsVigenteTrue(String transportistaId) {
        List<TarifaCosto> vigentes = tarifaCostoRepository.findByEsVigenteTrue();

        return vigentes.stream()
                .filter(t -> t.getTransportistaId() != null &&
                        t.getTransportistaId().equals(transportistaId))
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
        // Si llegó aquí pero es null (poco probable con WebClient reactivo, pero por si acaso)
        if (transportista == null) {
            return Optional.empty();
        }

        // --- INICIO CAMBIO: OBTENER HISTORIAL REAL ---
        // Buscar en el historial por transportistaId
        List<TarifaCostoHistorial> historialCompleto = historialRepository.findByTransportistaIdOrderByFechaModificacionDesc(id);

        // Mapear el historial a DTOs para el frontend
        List<HistorialServicioDTO> historialServicios = historialCompleto.stream()
                .map(HistorialServicioDTO::new) // Usar el constructor actualizado de HistorialServicioDTO
                .collect(Collectors.toList());
        // --- FIN CAMBIO: OBTENER HISTORIAL REAL ---

        // Obtener tarifas vigentes para sacar tipos de vehículo y zonas (como estaba antes)
        List<TarifaCosto> tarifasVigentes = findByTransportistaIdAndEsVigenteTrue(id);

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
                .filter(tipo -> tipoVehiculoIds.contains(tipo.getId()))
                .collect(Collectors.toSet());

        Set<ZonaViaje> zonasOperacion = tarifasVigentes.stream()
                .map(TarifaCosto::getZonaViaje)
                .filter(Objects::nonNull)
                .filter(ZonaViaje::getActivo) // Asegurar que la zona esté activa
                .collect(Collectors.toSet());


        // Pasar el historialServicios calculado al constructor del profile
        TransportistaProfile profile = new TransportistaProfile(transportista, tiposVehiculo, zonasOperacion, historialServicios);

        return Optional.of(profile);
    }

    @Data
    @NoArgsConstructor
    public static class TransportistaProfile {
        private String id;
        // CORRECCIÓN 1: Renombrar a nombreEmpresa
        private String nombreEmpresa;
        private String cuit;
        private String contactoNombre;
        private String contactoEmail;
        private TelefonoDTO contactoTelefono;
        private List<TipoVehiculoDTO> tiposVehiculo;
        private List<ZonaViajeDTO> zonasOperacion;
        // Añadir el campo para el historial
        private List<HistorialServicioDTO> historialServicios;

        // Constructor actualizado para recibir historialServicios
        public TransportistaProfile(TransportistaDTO transportista, Set<TipoVehiculoDTO> tiposVehiculo,
                Set<ZonaViaje> zonas, List<HistorialServicioDTO> historialServicios) {
            this.id = transportista.getId();
            // CORRECCIÓN 1: Usar getNombreComercial para asignar a nombreEmpresa
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
                     // Ordenar alfabéticamente para consistencia
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
                     // Ordenar alfabéticamente
                    .sorted(Comparator.comparing(ZonaViajeDTO::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());

            // Asignar el historial
            this.historialServicios = historialServicios != null ? historialServicios : Collections.emptyList();
        }
    }
}