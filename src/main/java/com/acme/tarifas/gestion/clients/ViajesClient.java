// Archivo: src/main/java/com/acme/tarifas/gestion/clients/ViajesClient.java
package com.acme.tarifas.gestion.clients;

import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.VehiculoDTO;
import com.acme.tarifas.gestion.dto.TransportistaDTO;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    // --- MÉTODOS EXISTENTES ---

    public List<TransportistaDTO> getTransportistas() {
        return webClient.get()
                .uri("/empresa") // Endpoint para obtener lista de transportistas/empresas
                .retrieve()
                .bodyToFlux(TransportistaDTO.class)
                .collectList()
                .block();
    }


    public TransportistaDTO getTransportistaById(String id) {
        return webClient.get()
                .uri("/empresa/{id}", id) // Endpoint para obtener un transportista/empresa por ID
                .retrieve()
                .bodyToMono(TransportistaDTO.class)
                .block();
    }


    public List<VehiculoDTO> getVehiculos(){ // Endpoint para obtener lista de vehículos
        return webClient.get()
                .uri("/vehiculo")
                .retrieve()
                .bodyToFlux(VehiculoDTO.class)
                .collectList()
                .block();
         }


    public VehiculoDTO getVehiculoById(String id){ // Endpoint para obtener un vehículo por ID
        return webClient.get()
                .uri("/vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(VehiculoDTO.class)
                .block();
    }


    public List<TipoVehiculoDTO> getTiposVehiculo(){ // Endpoint para obtener lista de tipos de vehículo
        return webClient.get()
                .uri("/tipo-vehiculo")
                .retrieve()
                .bodyToFlux(TipoVehiculoDTO.class)
                .collectList()
                .block();
    }

    public TipoVehiculoDTO getTiposVehiculoById(String id){ // Endpoint para obtener un tipo de vehículo por ID
        return webClient.get()
                .uri("/tipo-vehiculo/{id}", id)
                .retrieve()
                .bodyToMono(TipoVehiculoDTO.class)
                .block();
    }

    // --- MÉTODO MODIFICADO PARA DEVOLVER RESPUESTA COMPLETA ---
     public JsonNode getViajesFiltradosResponse(String vehiculoId, String fechaInicio, String fechaFin) { // Cambiado nombre y tipo de retorno
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("vehiculo", vehiculoId);         // Nombre según Swagger
        requestBody.put("fecha_desde", fechaInicio);     // Nombre según Swagger
        requestBody.put("fecha_hasta", fechaFin);       // Nombre según Swagger

        try {
            Mono<JsonNode> responseMono = webClient.post() // Usamos POST
                    .uri(uriBuilder -> uriBuilder
                            .path("/viaje-distribucion/buscar")
                            // Podríamos añadir un límite alto si la API no devuelve todos por defecto
                            // .queryParam("limit", "1000") // O un número suficientemente grande
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody)) // Enviar filtros en el cuerpo
                    .retrieve()
                    .bodyToMono(JsonNode.class); // Esperar un JsonNode genérico

            JsonNode response = responseMono.block();
            System.out.println("Respuesta recibida de /viaje-distribucion/buscar: " + (response != null ? response.toString() : "null")); // LOG para depurar
            return response; // Devolver el JsonNode completo

        } catch (Exception e) {
            System.err.println("Error al llamar POST /viaje-distribucion/buscar para vehículo " + vehiculoId +
                               " en rango [" + fechaInicio + " - " + fechaFin + "]. Error: " + e.getMessage());
            e.printStackTrace();
            // Devolver null o un JsonNode vacío podría ser una opción, o lanzar una excepción.
            // Por simplicidad, devolvemos null aquí, el servicio lo manejará.
            return null;
        }
    }
}