package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private TarifaCostoRepository tarifaCostoRepository;

    private double calcularCostoTotal(TarifaCosto tarifa) {
        double totalAdicionales = Optional.ofNullable(tarifa.getAdicionales())
                .orElse(Collections.emptyList())
                .stream()
                .mapToDouble(TarifaAdicional::getCostoEspecifico)
                .sum();

        return tarifa.getValorBase() + totalAdicionales;
    }
}