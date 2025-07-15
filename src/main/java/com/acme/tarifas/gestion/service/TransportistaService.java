package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.dao.VehiculoRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.dto.HistorialServicioDTO;
import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.entity.Vehiculo;
import com.acme.tarifas.gestion.entity.Viaje;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;
    private final ViajeRepository viajeRepository;
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public TransportistaService(TransportistaRepository transportistaRepository,
                                ViajeRepository viajeRepository,
                                VehiculoRepository vehiculoRepository) {
        this.transportistaRepository = transportistaRepository;
        this.viajeRepository = viajeRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<TransportistaProfile> getTransportistaProfile(Long id) {
        return transportistaRepository.findById(id).map(transportista -> {
            List<Vehiculo> vehiculos = vehiculoRepository.findByTransportistaPropietarioId(id);

            List<Viaje> viajes = viajeRepository.findByTransportistaId(id);
            List<HistorialServicioDTO> historial = viajes.stream()
                    .map(HistorialServicioDTO::new)
                    .collect(Collectors.toList());

            return new TransportistaProfile(transportista, vehiculos, historial);
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
        private List<VehiculoDTO> vehiculos;
        private List<ZonaViajeDTO> zonasOperacion;
        private List<HistorialServicioDTO> historialServicios;

        public TransportistaProfile(Transportista transportista, List<Vehiculo> vehiculos, List<HistorialServicioDTO> historial) {
            this.id = transportista.getId();
            this.nombreEmpresa = transportista.getNombreEmpresa();
            this.cuit = transportista.getCuit();
            this.contactoNombre = transportista.getContactoNombre();
            this.contactoEmail = transportista.getContactoEmail();
            this.contactoTelefono = transportista.getContactoTelefono();
            this.vehiculos = vehiculos.stream().map(VehiculoDTO::new).collect(Collectors.toList());
            this.zonasOperacion = transportista.getZonasOperacion().stream().map(ZonaViajeDTO::new).collect(Collectors.toList());
            this.historialServicios = historial;
        }
    }

    @Data
    @NoArgsConstructor
    public static class VehiculoDTO {
        private Long id;
        private String tipoVehiculo;

        public VehiculoDTO(Vehiculo vehiculo) {
            this.id = vehiculo.getId();
            if (vehiculo.getTipoVehiculo() != null) {
                this.tipoVehiculo = vehiculo.getTipoVehiculo().getNombre();
            }
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

    @Transactional
    public Transportista guardarTransportista(Transportista transportista) {
        if (transportistaRepository.existsByCuitAndActivoTrue(transportista.getCuit())) {
            throw new IllegalArgumentException("Ya existe un transportista activo con ese CUIT");
        }
        return transportistaRepository.save(transportista);
    }

    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }

    @Transactional
    public Optional<Transportista> actualizarTransportista(Long id, Transportista nuevosDatos) {
        return transportistaRepository.findById(id).map(existente -> {
            transportistaRepository.findByCuitAndActivoTrue(nuevosDatos.getCuit())
                    .ifPresent(duplicado -> {
                        if (!Objects.equals(duplicado.getId(), id)) {
                            throw new IllegalArgumentException("Ya existe otro transportista activo con ese CUIT");
                        }
                    });

            existente.setCuit(nuevosDatos.getCuit());
            existente.setNombreEmpresa(nuevosDatos.getNombreEmpresa());
            existente.setContactoNombre(nuevosDatos.getContactoNombre());
            existente.setContactoEmail(nuevosDatos.getContactoEmail());
            existente.setContactoTelefono(nuevosDatos.getContactoTelefono());
            existente.setActivo(nuevosDatos.isActivo());

            return transportistaRepository.save(existente);
        });
    }

    @Transactional
    public void eliminarTransportista(Long id) throws Exception {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));
        transportistaRepository.delete(transportista);
    }

    public Transportista baja(Long id) throws Exception {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));

        if (transportista.isActivo()) {
            transportista.setActivo(false);
            return transportistaRepository.save(transportista);
        } else {
            throw new Exception("El transportista ya está inactivo");
        }
    }
}