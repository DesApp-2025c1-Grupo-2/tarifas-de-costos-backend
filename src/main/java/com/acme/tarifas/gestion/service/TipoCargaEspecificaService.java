package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoCargaEspecificaRepository;
import com.acme.tarifas.gestion.entity.TipoCargaEspecifica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<TipoCargaEspecifica> obtenerPorId(Long id){
        return tipoCargaEspecificaRepository.findById(id);
    }

    public void eliminarTipoCargaEspecifica(Long id) throws Exception{
        TipoCargaEspecifica tipo = tipoCargaEspecificaRepository.findById(id)
                .orElseThrow(() -> new Exception("Tipo de carga no encontrado"));

        tipoCargaEspecificaRepository.deleteById(id);
    }
}