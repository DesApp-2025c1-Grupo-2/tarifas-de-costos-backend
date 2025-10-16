package com.acme.tarifas.gestion.clients;


import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class ViajesClient {
    //Sirve para consumir todo lo de la API de viajes.
    private final WebClient webClient;

    public ViajesClient(@Value("${VIAJES_URL}") String urlViajes, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(urlViajes).build();
    }

    public WebClient getWebClient() {
        return webClient;
    }


    public List<TransportistaDTO> getTransportistas() {
        return webClient.get()
                .uri("/empresa")
                .retrieve()
                .bodyToFlux(TransportistaDTO.class)
                .collectList()
                .block();
    }


    public TransportistaDTO getTransportistaById(String id) {
        return webClient.get()
                .uri("/empresa/{id}", id)
                .retrieve()
                .bodyToMono(TransportistaDTO.class)
                .block();
    }


    public List<VehiculoDTO> getVehiculos(){ //Vehiculos
        return webClient.get()
                .uri("/vehiculo")
                .retrieve()
                .bodyToFlux(VehiculoDTO.class)
                .collectList()
                .block();
         }


    public VehiculoDTO getVehiculoById(String id){
        return webClient.get()
                .uri("/vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(VehiculoDTO.class)
                .block();
    }


    public List<TipoVehiculoDTO> getTiposVehiculo(){ //TIPOS de vehiculo
        return webClient.get()
                .uri("/tipo-vehiculo")
                .retrieve()
                .bodyToFlux(TipoVehiculoDTO.class)
                .collectList()
                .block();
    }

    public TipoVehiculoDTO getTiposVehiculoById(String id){
        return webClient.get()
                .uri("/tipo-vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(TipoVehiculoDTO.class)
                .block();
    }

     public Long getCantidadViajesVehiculo(String vehiculoId, String fechaInicio, String fechaFin) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/viaje/buscar") 
                            .queryParam("vehiculoId", vehiculoId)
                            .queryParam("fechaInicio", fechaInicio)
                            .queryParam("fechaFin", fechaFin)
                            .build())
                    .retrieve()
                    .bodyToMono(Long.class) 
                    .block();
        } catch (Exception e) {
            System.err.println("Error al obtener cantidad de viajes del vehículo " + vehiculoId + " en rango de fechas. Error: " + e.getMessage());
            
            return 0L;
        }
    }
}





