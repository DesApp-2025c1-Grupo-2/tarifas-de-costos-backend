package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarifaHistorialService {

    @Autowired
    private TarifaCostoHistorialRepository historialRepository;

    public List<TarifaCostoHistorial> obtenerHistorialTarifa(Long tarifaId) {
        return historialRepository.findByTarifaOriginalId(tarifaId);
    }
}