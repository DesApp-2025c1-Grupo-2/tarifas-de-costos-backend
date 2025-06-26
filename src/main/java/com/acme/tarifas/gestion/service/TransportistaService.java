package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.dao.TarifaCostoRepository;
import com.acme.tarifas.gestion.dao.TransportistaRepository;
import com.acme.tarifas.gestion.entity.Transportista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private TarifaCostoRepository tarifaCostoRepository;

    @Transactional
    public Transportista guardarTransportista(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }

    @Transactional
    public Optional<Transportista> actualizarTransportista(Long id, Transportista nuevosDatos) {
        return transportistaRepository.findById(id).map(existente -> {
            existente.setNombreEmpresa(nuevosDatos.getNombreEmpresa());
            existente.setContactoNombre(nuevosDatos.getContactoNombre());
            existente.setContactoEmail(nuevosDatos.getContactoEmail());
            existente.setContactoTelefono(nuevosDatos.getContactoTelefono());
            existente.setEvaluacionDesempeno(nuevosDatos.getEvaluacionDesempeno());
            existente.setActivo(nuevosDatos.getActivo());
            return transportistaRepository.save(existente);
        });
    }

    @Transactional
    public void eliminarTransportista(Long id) throws Exception {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));

        transportistaRepository.delete(transportista);
    }

    @Transactional
    public Transportista baja(Long id) throws Exception {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transportista no encontrado"));

        if (Boolean.TRUE.equals(transportista.getActivo())) {
            transportista.setActivo(false);
            return transportistaRepository.save(transportista);
        } else {
            throw new Exception("El transportista ya estÃ¡ inactivo");
        }
    }

    public Map<String, Object> analizarTarifasTransportista(Long idTransportista) {
        Transportista transportista = transportistaRepository.findById(idTransportista)
                .orElseThrow(() -> new ExpressionException("Transportista no encontrado"));

        List<Object[]> estadisticas = tarifaCostoRepository.obtenerEstadisticasTarifasPorTransportista(idTransportista);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("transportista", transportista);

        if (!estadisticas.isEmpty()) {
            Object[] stats = estadisticas.get(0);
            resultado.put("promedioTarifas", stats[0]);
            resultado.put("tarifaMinima", stats[1]);
            resultado.put("tarifaMaxima", stats[2]);
            resultado.put("cantidadTarifas", stats[3]);
        }

        return resultado;
    }

    public List<Map<String, Object>> compararCostosTransportistas() {
        List<Object[]> datos = tarifaCostoRepository.obtenerComparativaCostosTransportistas();

        return datos.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("transportistaId", item[0]);
            map.put("nombreEmpresa", item[1]);
            map.put("promedioTarifas", item[2]);
            map.put("tarifaMinima", item[3]);
            map.put("tarifaMaxima", item[4]);
            map.put("cantidadTarifas", item[5]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Transportista> obtenerTransportistasOrdenadosPorCostoPromedio() {
        List<Long> idsOrdenados = tarifaCostoRepository.obtenerIdsTransportistasOrdenadosPorCostoPromedio();
        Map<Long, Transportista> transportistas = transportistaRepository.findAllById(idsOrdenados)
                .stream()
                .collect(Collectors.toMap(Transportista::getId, t -> t));

        return idsOrdenados.stream()
                .map(transportistas::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> analizarRelacionPrecioCalidad() {
        List<Object[]> datos = tarifaCostoRepository.obtenerDatosParaRelacionPrecioCalidad();

        return datos.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("transportistaId", item[0]);
            map.put("nombreEmpresa", item[1]);
            map.put("evaluacionDesempeno", item[2]);
            
            // Aseguramos que el promedio no sea null y casteamos bien
            double promedio = 0.0;
            if (item[3] != null) {
                if (item[3] instanceof Number) {
                    promedio = ((Number) item[3]).doubleValue();
                }
            }
            map.put("promedioTarifas", promedio);

            String evaluacion = (String) item[2];
            double indice = promedio;

            if (evaluacion != null) {
                if (evaluacion.contains("Excelente")) {
                    indice = promedio * 0.8;
                } else if (evaluacion.contains("Bueno")) {
                    indice = promedio * 0.9;
                }
            }

            map.put("indiceRelacion", indice);
            return map;
        }).collect(Collectors.toList());
    }
}