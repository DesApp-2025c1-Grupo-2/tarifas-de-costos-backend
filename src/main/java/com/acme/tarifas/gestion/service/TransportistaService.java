package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.dto.TelefonoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.*;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//Refaccionar utilizando ViajesClient
//Eliminar todo lo que sea para creación, modificacion y eliminación. Dejar solo visualizacion/filtrado
@Service
public class TransportistaService {

    private final ViajeRepository viajeRepository;
    private final TarifaCostoRepository tarifaCostoRepository;

    private final ViajesClient viajesClient;

    @Autowired
    public TransportistaService(ViajeRepository viajeRepository,
            TarifaCostoRepository tarifaCostoRepository,
            ViajesClient viajesClient) {

        this.viajeRepository = viajeRepository;
        this.tarifaCostoRepository = tarifaCostoRepository;
        this.viajesClient = viajesClient;
    }

    public List<TarifaCosto> findByTransportistaIdAndEsVigenteTrue(String transportistaId) {
        List<TarifaCosto> vigentes = tarifaCostoRepository.findByEsVigenteTrue();

        return vigentes.stream()
                .filter(t -> t.getTransportistaId() != null &&
                        t.getTransportistaId().equals(transportistaId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<TransportistaProfile> getTransportistaProfile(String id) {
        TransportistaDTO transportista = viajesClient.getTransportistaById(id);
        if (transportista == null) {
            return Optional.empty();
        }

        List<TarifaCosto> tarifas = findByTransportistaIdAndEsVigenteTrue(id);

        Set<String> tipoVehiculoIds = tarifas.stream()
                .map(TarifaCosto::getTipoVehiculoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<TipoVehiculoDTO> allTipos = viajesClient.getTiposVehiculo();
        Set<TipoVehiculoDTO> tiposVehiculo = allTipos.stream()
                .filter(tipo -> tipoVehiculoIds.contains(tipo.getId()))
                .collect(Collectors.toSet());

        Set<ZonaViaje> zonasOperacion = tarifas.stream()
                .map(TarifaCosto::getZonaViaje)
                .filter(Objects::nonNull)
                .filter(ZonaViaje::isActivo)
                .collect(Collectors.toSet());

        TransportistaProfile profile = new TransportistaProfile(transportista, tiposVehiculo, zonasOperacion);

        return Optional.of(profile);
    }

    @Data
    @NoArgsConstructor
    public static class TransportistaProfile {
        private String id;
        private String nombreComercial;
        private String cuit;
        private String contactoNombre;
        private String contactoEmail;
        private TelefonoDTO contactoTelefono;
        private List<TipoVehiculoDTO> tiposVehiculo;
        private List<ZonaViajeDTO> zonasOperacion;

        public TransportistaProfile(TransportistaDTO transportista, Set<TipoVehiculoDTO> tiposVehiculo,
                Set<ZonaViaje> zonas) {
            this.id = transportista.getId();
            this.nombreComercial = transportista.getNombreComercial();
            this.cuit = transportista.getCuit();
            this.contactoNombre = transportista.getContacto().getNombre();
            this.contactoEmail = transportista.getContacto().getEmail();
            this.contactoTelefono = transportista.getContacto().getTelefono();
            this.tiposVehiculo = tiposVehiculo.stream()
                    .filter(Objects::nonNull)
                    .map(tv -> {
                        TipoVehiculoDTO dto = new TipoVehiculoDTO();
                        dto.setId(tv.getId());
                        dto.setNombre(tv.getNombre());
                        dto.setDescripcion(tv.getDescripcion());
                        dto.setDeletedAt(tv.getDeletedAt());
                        dto.setLicenciaPermitida(tv.getLicenciaPermitida());
                        return dto;
                    })
                    .collect(Collectors.toList());

            this.zonasOperacion = zonas.stream()
                    .filter(Objects::nonNull)
                    .map(ZonaViajeDTO::new)
                    .collect(Collectors.toList());
        }
    }

}