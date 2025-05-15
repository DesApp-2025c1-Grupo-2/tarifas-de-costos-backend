package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoCargaTarifaRepository;
import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCargaTarifaService {

    @Autowired
    private TipoCargaTarifaRepository tipoCargaTarifaRepository;

    public TipoCargaTarifa guardarTipoCargaTarifa(TipoCargaTarifa tipo) {
        return tipoCargaTarifaRepository.save(tipo);
    }

    public List<TipoCargaTarifa> obtenerTodosTiposCargaTarifa() {
        return tipoCargaTarifaRepository.findAll();
    }
}