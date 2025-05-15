package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoCargaEspecificaRepository;
import com.acme.tarifas.gestion.entity.TipoCargaEspecifica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCargaEspecificaService {

    @Autowired
    private TipoCargaEspecificaRepository tipoCargaEspecificaRepository;

    public TipoCargaEspecifica guardarTipoCargaEspecifica(TipoCargaEspecifica tipo) {
        return tipoCargaEspecificaRepository.save(tipo);
    }

    public List<TipoCargaEspecifica> obtenerTodosTiposCargaEspecifica() {
        return tipoCargaEspecificaRepository.findAll();
    }
}