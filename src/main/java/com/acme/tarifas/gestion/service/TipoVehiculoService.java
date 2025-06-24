package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoVehiculoRepository;
import com.acme.tarifas.gestion.entity.TipoVehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @Transactional
    public TipoVehiculo guardarTipo(TipoVehiculo tipo) {
        return tipoVehiculoRepository.save(tipo);
    }

    public List<TipoVehiculo> obtenerTodos() {
        return tipoVehiculoRepository.findAll();
    }

    public Optional<TipoVehiculo> obtenerPorId(Long id) {
        return tipoVehiculoRepository.findById(id);
    }

    public Optional<TipoVehiculo> actualizarTipo(Long id, TipoVehiculo nuevosDatos){
        return tipoVehiculoRepository.findById(id).map( existente ->{
            existente.setCapacidadPesoKG(nuevosDatos.getCapacidadPesoKG());
            existente.setDescripcion(nuevosDatos.getDescripcion());
            existente.setNombre(nuevosDatos.getNombre());
            existente.setCapacidadVolumenM3(nuevosDatos.getCapacidadVolumenM3());
            return tipoVehiculoRepository.save(existente);
        });
    }

    @Transactional
    public void eliminarTipo(Long id) {
        tipoVehiculoRepository.deleteById(id);
    }
}