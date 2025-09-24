package com.acme.tarifas.gestion.clients;


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


    public List<JsonNode> getCamiones(){ //Vehiculos
        return webClient.get()
                .uri("/vehiculo")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList()
                .block();
         }

    public JsonNode getCamionById(String id){
        return webClient.get()
                .uri("/vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }


    public List<JsonNode> getTiposVehiculo(){ //TIPOS de vehiculo
        return webClient.get()
                .uri("/tipo-vehiculo")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList()
                .block();
    }

    public JsonNode getTiposVehiculoById(String id){
        return webClient.get()
                .uri("/tipo-vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

}


