package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.VehiculoDetalleRepository;
import com.acme.tarifas.gestion.entity.VehiculoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class VehiculoDetalleService {

    @Autowired
    private VehiculoDetalleRepository repository;

    public Optional<VehiculoDetalle> obtenerPorVehiculoId(String vehiculoId) {
        return repository.findByVehiculoId(vehiculoId);
    }

    @Transactional
    public VehiculoDetalle guardar(VehiculoDetalle detalle) {
        Optional<VehiculoDetalle> existenteOpt = repository.findByVehiculoId(detalle.getVehiculoId());
        if (existenteOpt.isPresent()) {
            VehiculoDetalle existente = existenteOpt.get();
            existente.setLitrosPorTanque(detalle.getLitrosPorTanque());
            existente.setKmPorTanque(detalle.getKmPorTanque());
            existente.setTipoCombustible(detalle.getTipoCombustible());
            return repository.save(existente);
        } else {
            return repository.save(detalle);
        }
    }
}