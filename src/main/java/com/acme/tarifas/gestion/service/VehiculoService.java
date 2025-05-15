package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.VehiculoRepository;
import com.acme.tarifas.gestion.entity.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Transactional
    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    public List<Vehiculo> obtenerTodosVehiculos() {
        return vehiculoRepository.findAll();
    }

    public List<Vehiculo> obtenerVehiculosPorTransportista(Long transportistaId) {
        return vehiculoRepository.findByTransportistaPropietarioId(transportistaId);
    }
}