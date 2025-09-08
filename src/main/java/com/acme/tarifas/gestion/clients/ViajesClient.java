package com.acme.tarifas.gestion.clients;

import com.acme.tarifas.gestion.dto.TransportistaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class ViajesClient {

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

    /*
    Cuando necesite el campo 'telefono' hay que hacer lo siguiente:
    String numeroCompleto = "+" + contacto.getTelefono().getCodigoPais()
                       + "-" + contacto.getTelefono().getCodigoArea()
                       + "-" + contacto.getTelefono().getNumero();

     porque VIAJES tiene 'telefono' como un objeto, osea que solo nos llega la referencia mediante un id de mongo

     */

    /*
    // Obtener un viaje por ID
    public ViajeDto getViajePorId(Long id) {
        return webClient.get()
                .uri("/viajes/{id}", id)
                .retrieve()
                .bodyToMono(ViajeDto.class)
                .block();
    }
    */

}
