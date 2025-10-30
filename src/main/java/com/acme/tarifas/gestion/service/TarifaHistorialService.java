
package com.acme.tarifas.gestion.service;

import com.acme.tarifas.gestion.clients.ViajesClient; 
import com.acme.tarifas.gestion.dao.TarifaCostoHistorialRepository;
import com.acme.tarifas.gestion.dto.TarifaCostoHistorialDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO; 
import com.acme.tarifas.gestion.entity.TarifaCostoHistorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections; 
import java.util.List;
import java.util.Map;          
import java.util.function.Function; 
import java.util.stream.Collectors;

@Service
public class TarifaHistorialService {

    @Autowired
    private TarifaCostoHistorialRepository historialRepository;

    @Autowired
    private ViajesClient viajesClient; 

    @Transactional(readOnly = true)
    public List<TarifaCostoHistorialDTO> obtenerHistorialTarifa(Long tarifaId) {
        List<TarifaCostoHistorial> historialEntities = historialRepository.findByTarifaOriginalId(tarifaId);

        if (historialEntities.isEmpty()) {
            return Collections.emptyList(); 
        }


        Map<String, String> transportistaNombres;
        try {
            transportistaNombres = viajesClient.getTransportistas().stream()
                    .collect(Collectors.toMap(
                            TransportistaDTO::getId,
                            TransportistaDTO::getNombreComercial, 
                            (existing, replacement) -> existing 
                    ));
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudieron obtener los nombres de transportistas para el historial. Se usarán IDs.");
            transportistaNombres = Collections.emptyMap(); 
        }

      
        Map<String, String> finalTransportistaNombres = transportistaNombres; 
        return historialEntities.stream()
                .map(entity -> {
                    
                    String nombre = finalTransportistaNombres.getOrDefault(entity.getTransportistaId(), "ID: " + entity.getTransportistaId());
                    return new TarifaCostoHistorialDTO(entity, nombre); //
                })
                .collect(Collectors.toList());
    }
}