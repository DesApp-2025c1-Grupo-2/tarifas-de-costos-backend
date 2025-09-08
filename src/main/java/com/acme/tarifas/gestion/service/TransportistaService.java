package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.dto.HistorialServicioDTO;
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

    private final TransportistaRepository transportistaRepository;
    private final ViajeRepository viajeRepository;
    private final TarifaCostoRepository tarifaCostoRepository;

    @Autowired
    public TransportistaService(TransportistaRepository transportistaRepository,
                                ViajeRepository viajeRepository,
                                TarifaCostoRepository tarifaCostoRepository) {
        this.transportistaRepository = transportistaRepository;
        this.viajeRepository = viajeRepository;
        this.tarifaCostoRepository = tarifaCostoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<TransportistaProfile> getTransportistaProfile(Long id) {
        //Revisar
        return transportistaRepository.findById(id).map(transportista -> {

            List<TarifaCosto> tarifas = tarifaCostoRepository.findByTransportistaIdAndEsVigenteTrue(id);

            Set<TipoVehiculo> tiposVehiculo = tarifas.stream()
                    .map(TarifaCosto::getTipoVehiculo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<ZonaViaje> zonasOperacion = tarifas.stream()
                    .map(TarifaCosto::getZonaViaje)
                    .filter(Objects::nonNull)
                    .filter(ZonaViaje::isActivo)
                    .collect(Collectors.toSet());

            List<Viaje> viajes = viajeRepository.findByTransportistaId(id).stream()
                    .filter(viaje -> viaje.getTarifaCostoUtilizada() != null && viaje.getTarifaCostoUtilizada().isEsVigente())
                    .collect(Collectors.toList());
            List<HistorialServicioDTO> historial = viajes.stream()
                    .map(HistorialServicioDTO::new)
                    .collect(Collectors.toList());

            return new TransportistaProfile(transportista, tiposVehiculo, zonasOperacion, historial);
        });
    }

    @Data
    @NoArgsConstructor
    public static class TransportistaProfile {
        private Long id;
        private String nombreEmpresa;
        private String cuit;
        private String contactoNombre;
        private String contactoEmail;
        private String contactoTelefono;
        private List<TipoVehiculoDTO> vehiculos;
        private List<ZonaViajeDTO> zonasOperacion;
        private List<HistorialServicioDTO> historialServicios;

        public TransportistaProfile(Transportista transportista, Set<TipoVehiculo> tiposVehiculo, Set<ZonaViaje> zonas, List<HistorialServicioDTO> historial) {
            this.id = transportista.getId();
            this.nombreEmpresa = transportista.getNombreEmpresa();
            this.cuit = transportista.getCuit();
            this.contactoNombre = transportista.getContactoNombre();
            this.contactoEmail = transportista.getContactoEmail();
            this.contactoTelefono = transportista.getContactoTelefono();
            this.vehiculos = tiposVehiculo.stream().map(TipoVehiculoDTO::new).collect(Collectors.toList());
            this.zonasOperacion = zonas.stream().map(ZonaViajeDTO::new).collect(Collectors.toList());
            this.historialServicios = historial;
        }
    }

    @Data
    @NoArgsConstructor
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



    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }





}