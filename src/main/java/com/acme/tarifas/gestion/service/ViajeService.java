package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.VehiculoRepository;
import com.acme.tarifas.gestion.dao.ViajeRepository;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.entity.Viaje;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private TarifaCostoRepository tarifaCostoRepository;

    /*
    se eliminó clientes
    @Autowired
    private ClienteRepository clienteRepository;
    */
    @Autowired
    private VehiculoRepository vehiculoRepository;

    /*
    public Map<String, Double> calcularRentabilidadPorPeriodo(LocalDate inicio, LocalDate fin) {
        List<Viaje> viajes = viajeRepository.findByFechaViajeBetween(inicio, fin);

        double totalFacturado = viajes.stream()
                .mapToDouble(Viaje::getPrecioFacturadoCliente)
                .sum();

        double totalCostos = viajes.stream()
                .mapToDouble(v -> {
                    TarifaCosto tarifa = tarifaCostoRepository.findById(v.getTarifaCostoUtilizada().getId())
                            .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
                    return calcularCostoTotal(tarifa);
                })
                .sum();

        Map<String, Double> resultados = new HashMap<>();
        resultados.put("totalFacturado", totalFacturado);
        resultados.put("totalCostos", totalCostos);
        resultados.put("margen", totalFacturado - totalCostos);

        return resultados;
    }
    */
    private double calcularCostoTotal(TarifaCosto tarifa) {
        double totalAdicionales = Optional.ofNullable(tarifa.getAdicionales())
                .orElse(Collections.emptyList())
                .stream()
                .mapToDouble(TarifaAdicional::getCostoEspecifico)
                .sum();

        return tarifa.getValorBase() + totalAdicionales;
    }}

    /*
    @Transactional
    public Viaje registrarViaje(Viaje viaje) {
        validarViaje(viaje);

        viaje.setFechaRegistroSistema(LocalDateTime.now());
        return viajeRepository.save(viaje);
    }
    */


    /*
    private void validarViaje(Viaje viaje) {
        se eliminó cliente
        if (!clienteRepository.existsById(viaje.getCliente().getId())) {
            throw new EntityNotFoundException("Cliente no encontrado");
        }

        if (!tarifaCostoRepository.existsById(viaje.getTarifaCostoUtilizada().getId())) {
            throw new EntityNotFoundException("Tarifa no encontrada");
        }

        if (viaje.getVehiculoUtilizado() != null &&
                !vehiculoRepository.existsById(viaje.getVehiculoUtilizado().getId())) {
            throw new EntityNotFoundException("Vehículo no encontrado");
        }
    }
}
/*
     */