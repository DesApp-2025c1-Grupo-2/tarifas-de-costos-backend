package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.dto.TelefonoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;
import com.acme.tarifas.gestion.entity.*;
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

        TransportistaDTO transportista = viajesClient.getTransportistaById(transportistaId);


        List<TarifaCosto> vigentes = tarifaCostoRepository.findByEsVigenteTrue();


        return vigentes.stream()
                .filter(t -> t.getTransportista() != null &&
                        t.getTransportista().getId().equals(transportista.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<TransportistaProfile> getTransportistaProfile(String id) {
        TransportistaDTO transportista = viajesClient.getTransportistaById(id);
        if (transportista == null) {
            return Optional.empty();
        }


        List<TarifaCosto> tarifas = findByTransportistaIdAndEsVigenteTrue(id);

        Set<TipoVehiculo> tiposVehiculo = tarifas.stream()
                .map(TarifaCosto::getTipoVehiculo)
                .filter(Objects::nonNull)
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
        private List<TipoVehiculoDTO> vehiculos;
        private List<ZonaViajeDTO> zonasOperacion;

        public TransportistaProfile(TransportistaDTO transportista, Set<TipoVehiculo> tiposVehiculo, Set<ZonaViaje> zonas) {
            this.id = transportista.getId();
            this.nombreComercial = transportista.getNombreComercial();
            this.cuit = transportista.getCuit();
            this.contactoNombre = transportista.getContacto().getNombre();
            this.contactoEmail = transportista.getContacto().getEmail();
            this.contactoTelefono = transportista.getContacto().getTelefono();
            this.vehiculos = tiposVehiculo.stream().map(TipoVehiculoDTO::new).collect(Collectors.toList()); //cambiar
            this.zonasOperacion = zonas.stream().map(ZonaViajeDTO::new).collect(Collectors.toList()); //cambiar
        }
    }

    @Data
    @NoArgsConstructor //Tipo vehiculo ahora viene de api viajes, revisar
    public static class TipoVehiculoDTO {
        private Long id;
        private String nombre;

        public TipoVehiculoDTO(TipoVehiculo tipoVehiculo) {
            this.id = tipoVehiculo.getId();
            this.nombre = tipoVehiculo.getNombre();
        }
    }

    @Data
    @NoArgsConstructor
    public static class ZonaViajeDTO {
         private Long id;
         private String nombre;

         public ZonaViajeDTO(ZonaViaje zona) {
             this.id = zona.getId();
             this.nombre = zona.getNombre();
         }
    }

}