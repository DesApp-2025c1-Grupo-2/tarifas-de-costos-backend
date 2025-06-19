package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TipoCargaTarifaRepository;
import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import com.acme.tarifas.gestion.entity.Transportista;
import com.acme.tarifas.gestion.entity.ZonaViaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<TipoCargaTarifa> obtenerPorId(Long id){
        return tipoCargaTarifaRepository.findById(id);
    }

    public void eliminarTipoCargaTarifa(Long id)throws Exception{
        TipoCargaTarifa tipo = tipoCargaTarifaRepository.findById(id)
                .orElseThrow(() -> new Exception("Tipo de carga no encontrado"));

        tipoCargaTarifaRepository.delete(tipo);
    }

    public TipoCargaTarifa baja(Long id) throws Exception{
        TipoCargaTarifa tipoCarga = tipoCargaTarifaRepository.findById(id)
                .orElseThrow(() -> new Exception("tipo de carga no encontrada"));

        if(tipoCarga.getActivo()){
            tipoCarga.setActivo(false);
            return tipoCargaTarifaRepository.save(tipoCarga);
        }else{
            throw new Exception("El tipo de carga ya está inactivo");
        }
    }
}